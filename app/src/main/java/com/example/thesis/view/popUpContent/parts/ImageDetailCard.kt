package com.example.thesis.view.popUpContent.parts

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.thesis.model.data.mapTracking.PhotoUploadResult

@Composable
fun ImageDetailsCard(result: PhotoUploadResult?) {
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
                    DetailBox("Frame ID", result?.frameId ?: "N/A", Modifier.weight(1f))
                    DetailBox(
                        "Processing Time",
                        result?.processingTimeMs?.let { "${it} ms" } ?: "N/A",
                        Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailBox(
                        "Image Saved",
                        if (result?.savedImageUri != null) "Yes" else "No",
                        Modifier.weight(1f)
                    )
                    DetailBox("Status", "Processed", Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailBox(
                        "Detections",
                        result?.inferenceData ?: "N/A",
                        Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}