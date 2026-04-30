package com.example.thesis.view.popUpContent.parts

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesis.model.data.mapTracking.PhotoUploadResult
import com.example.thesis.model.`object`.LocalMarkerStore
import org.json.JSONObject
import kotlin.math.abs

fun getSeverityLabel(count: Int): String {
    return when {
        count == 0   -> "No Damage"
        count <= 2   -> "Low"
        count <= 5   -> "Medium"
        count <= 7   -> "High"
        else         -> "Severely Damaged"
    }
}

fun getSeverityColor(count: Int): Color {
    return when {
        count == 0   -> Color(0xFF9E9E9E) // Gray
        count <= 2   -> Color(0xFF4CAF50) // Green
        count <= 5   -> Color(0xFFFF9800) // Orange
        count <= 7   -> Color(0xFFF44336) // Red
        else         -> Color(0xFF7B0000) // Dark Red
    }
}

fun convertDecimalToDMS(decimal: Double?, isLatitude: Boolean): String {
    if (decimal == null) return "N/A"
    val absDecimal = abs(decimal)
    val degrees = absDecimal.toInt()
    val minutesDecimal = (absDecimal - degrees) * 60
    val minutes = minutesDecimal.toInt()
    val seconds = (minutesDecimal - minutes) * 60
    
    val direction = if (isLatitude) {
        if (decimal >= 0) "N" else "S"
    } else {
        if (decimal >= 0) "E" else "W"
    }
    
    return "%d°%d'%.1f\"%s".format(degrees, minutes, seconds, direction)
}

@Composable
fun ImageDetailsCard(result: PhotoUploadResult?, imageUri: Uri?) {
    // Collect markers concurrently from the LocalMarkerStore
    val markers by LocalMarkerStore.markersFlow.collectAsState()
    
    // Find the marker matching this image URI to get coordinates concurrently
    val matchingMarker = remember(imageUri, markers) {
        markers.find { it.imageUrl == imageUri?.toString() }
    }

    val latitude = matchingMarker?.latitude ?: result?.latitude
    val longitude = matchingMarker?.longitude ?: result?.longitude

    val fileName = imageUri?.lastPathSegment ?: "N/A"
    
    val parsedDetections = result?.inferenceData?.let { data ->
        try {
            if (data.isBlank()) return@let null
            val json = JSONObject(data)
            val detections = json.getJSONArray("detections")
            val count = json.optInt("count", 0)

            val list = mutableListOf<String>()
            for (i in 0 until detections.length()) {
                val det = detections.getJSONObject(i)
                val label = det.optString("label", "Unknown")
                list.add(label)
            }
            Pair(list, count)
        } catch (e: Exception) {
            Log.e("ImageDetailsCard", "Error parsing JSON: ${e.message}")
            null
        }
    }

    val detectionCount = parsedDetections?.second ?: 0
    val severityLabel = getSeverityLabel(detectionCount)
    val severityColor = getSeverityColor(detectionCount)
    
    val defectValue = if (parsedDetections != null && parsedDetections.first.isNotEmpty()) {
        parsedDetections.first.distinct().joinToString(", ")
    } else {
        "No Defects Found"
    }

    Column {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Top Row: Defect and Severity Level
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailBox("Defect", defectValue, Modifier.weight(1.2f))
                    DetailBox(
                        "Severity Level",
                        severityLabel,
                        Modifier.weight(1f),
                        valueColor = severityColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Status and Processing Info Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                    Text(
                        text = "Processing Time: ${result?.processingTimeMs?.let { "$it ms" } ?: "N/A"}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Coordinate Table Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(80.dp))
                    Text(
                        text = "DECIMAL",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "DMS",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Latitude Row
                CoordinateRow(label = "Latitude", decimalValue = latitude?.let { "%.6f".format(it) } ?: "N/A", dmsValue = convertDecimalToDMS(latitude, true))

                Spacer(modifier = Modifier.height(12.dp))

                // Longitude Row
                CoordinateRow(label = "Longitude", decimalValue = longitude?.let { "%.6f".format(it) } ?: "N/A", dmsValue = convertDecimalToDMS(longitude, false))

                Spacer(modifier = Modifier.height(8.dp))
                
                // Footer Info
                Text(
                    text = "ID: ${result?.frameId ?: fileName}",
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun CoordinateRow(label: String, decimalValue: String, dmsValue: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
            modifier = Modifier.width(80.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .background(
                    color = Color(0xFFF2F2F2),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = decimalValue,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Black
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(20.dp)
                    .background(Color.LightGray)
            )

            Text(
                text = dmsValue,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}