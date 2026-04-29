package com.example.thesis.view.popUpContent.parts

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesis.model.data.mapTracking.PhotoUploadResult
import org.json.JSONObject

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

@Composable
fun ImageDetailsCard(result: PhotoUploadResult?, imageUri: Uri?) {
    Log.d("ImageDetailsCard", "Recomposing with result=$result, uri=$imageUri")
    Log.d("ImageDetailsCard", "Inference data to parse: ${result?.inferenceData}")

    val fileName = imageUri?.lastPathSegment ?: "N/A"
    val hasProcessedMedia = result?.savedImageUri != null || imageUri != null

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
                val confidence = det.optDouble("confidence", 0.0)
                list.add("$label (${"%.2f".format(confidence * 100)}%)")
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
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailBox("Frame ID", result?.frameId ?: fileName, Modifier.weight(1f))
                    DetailBox(
                        "Processing Time",
                        result?.processingTimeMs?.let { "${it} ms" } ?: "N/A",
                        Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailBox(
                        "Image Saved",
                        if (hasProcessedMedia) "Yes" else "No",
                        Modifier.weight(1f)
                    )
                    // Severity box with dynamic color
                    DetailBox(
                        label = "Status: Severity",
                        value = severityLabel,
                        modifier = Modifier.weight(1f),
                        valueColor = severityColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Detections",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )

                if (parsedDetections != null) {
                    Text(
                        text = "Total Count: ${parsedDetections.second}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    parsedDetections.first.forEach { detection ->
                        Text(
                            text = "• $detection",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 2.dp, start = 8.dp)
                        )
                    }
                } else {
                    Text(
                        text = result?.inferenceData?.takeIf { it.isNotBlank() }
                            ?: "No inference details available",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}