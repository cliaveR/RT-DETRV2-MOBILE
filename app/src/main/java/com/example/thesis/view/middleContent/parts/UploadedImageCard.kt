package com.example.thesis.view.middleContent.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UploadedImage(
    val id: String,
    val fileName: String,
    val uploadDate: LocalDateTime
    // add more fields later: uri, size, resolution, etc.
)

@Preview(showBackground = true)
@Composable
fun UploadedImageCardPreview() {
    val sample = UploadedImage(
        id = "img_001",
        fileName = "cracks1021312",
        uploadDate = LocalDateTime.now().minusDays(2)
    )
    UploadedImageCard(
        image = sample,
        onClick = {},
        onMoreClick = {}
    )
}

@Composable
fun UploadedImageCard(
    image: UploadedImage,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = "Uploaded image preview",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = image.fileName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Upload Date: ${image.uploadDate.format(formatter)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Share") },
                    onClick = { expanded = false /* share */ }
                )
                DropdownMenuItem(
                    text = { Text("Delete") },
                    onClick = { expanded = false /* delete */ }
                )
            }
        }
    }
}