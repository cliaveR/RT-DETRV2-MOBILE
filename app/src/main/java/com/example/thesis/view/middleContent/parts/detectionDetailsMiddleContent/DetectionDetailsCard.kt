package com.example.thesis.view.middleContent.parts.detectionDetailsMiddleContent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.example.thesis.Map.MapUtils
import com.example.thesis.Map.MapUtils.createMap
import com.arcgismaps.Color as ArcColor


@Preview
@Composable
fun DetectionDetailsCard() {

    val isPreview = LocalInspectionMode.current

    // Only initialize ArcGIS stuff when NOT in preview
    val map = if (!isPreview) remember { createMap() } else null
    val graphicsOverlay = if (!isPreview) remember {
        GraphicsOverlay().apply {
            graphics.addAll(
                listOf(
                    MapUtils.createPointGraphic(120.371082, 17.595492, ArcColor.red),
                    MapUtils.createPointGraphic(120.399521, 17.581936, ArcColor.green),
                    MapUtils.createPointGraphic(120.389398, 17.589375, ArcColor.red)
                )
            )
        }
    } else null

    var defect by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var roadSection by remember { mutableStateOf("") }
    var station by remember { mutableStateOf("") }
    var severityLevel by remember { mutableStateOf("") }
    var showGuide by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UploadResultTitleText()

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InputBox("Defect", defect, { defect = it }, Modifier.weight(1f))
                InputBox("Quantity", quantity, { quantity = it }, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InputBox("Road Section", roadSection, { roadSection = it }, Modifier.weight(1f))
                InputBox("Station", station, { station = it }, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            ReadOnlySeverityDisplay(
                severityValue = severityLevel,
                onInfoClick = { showGuide = true }
            )

            if (showGuide) {
                SeverityGuidePopup(onDismiss = { showGuide = false })
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Map View / Placeholder ---
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F2F2))
            ) {
                if (isPreview || map == null || graphicsOverlay == null) {
                    // Preview-safe placeholder
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🗺️ Map Preview Unavailable",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    MapView(
                        modifier = Modifier.fillMaxSize(),
                        arcGISMap = map,
                        graphicsOverlays = listOf(graphicsOverlay)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            CoordinateTable()

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    /* TODO: Handle confirmation logic here */
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF000000),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Confirm Details",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InputBox(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF2F2F2),
                focusedContainerColor = Color(0xFFF2F2F2),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Gray
            )
        )
    }
}

@Composable
fun CoordinateTable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Spacer(modifier = Modifier.weight(0.3f))
            Text(
                text = "DECIMAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "DMS",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }

        CoordinateRow("Latitude")
        CoordinateRow("Longitude")
    }
}

@Composable
fun CoordinateRow(label: String) {
    var decimalValue by remember { mutableStateOf("") }
    var dmsValue by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray,
            modifier = Modifier.weight(0.4F)
        )

        Row(
            modifier = Modifier
                .weight(2f)
                .background(
                    color = Color(0xFFF2F2F2),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoordinateInputField(
                value = decimalValue,
                onValueChange = { decimalValue = it },
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(Color.LightGray)
            )

            CoordinateInputField(
                value = dmsValue,
                onValueChange = { dmsValue = it },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CoordinateInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.padding(horizontal = 8.dp),
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.Black
        ),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    "",
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        color = Color.LightGray,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            innerTextField()
        }
    )
}

@Composable
fun UploadResultTitleText() {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = "Road Damage Details",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ReadOnlySeverityDisplay(severityValue: String, onInfoClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(
                text = "Severity Level",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onInfoClick,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Show Guide",
                    tint = Color.Gray
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = severityValue,
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun SeverityGuidePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it", color = Color.Black)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Severity Guide", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text("• Level 1: Minor (1 Damages Detected)", fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Text("• Level 2: Moderate (2-3 Damages Detected)", fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Text("• Level 3: Severe (>3 Damages Detected)", fontSize = 14.sp)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
}