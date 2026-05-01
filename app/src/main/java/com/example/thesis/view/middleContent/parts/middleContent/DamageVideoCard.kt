package com.example.thesis.view.middleContent.parts.middleContent

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesis.model.data.DamageVideoItem
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DamageVideoCard(
    video: DamageVideoItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    // Log when the card is composed to verify data exists
    LaunchedEffect(video.uri) {
        Log.d("DamageVideoCard", "Card composed for video: ${video.displayName}, URI: ${video.uri}")
    }

    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a")
    val formattedDate = video.dateAddedSeconds?.let {
        Instant.ofEpochSecond(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(formatter)
    } ?: "Unknown date"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { 
                Log.d("DamageVideoCard", "Card CLICKED: ${video.displayName}")
                onClick() 
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Video Thumbnail Placeholder / Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Video",
                    tint = Color(0xFF0084FF),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                
                if (video.processingTimeMs != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFFF0F7FF),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Processed in ${video.processingTimeMs}ms",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = Color(0xFF0084FF),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            onDeleteClick()
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) }
                    )
                }
            }
        }
    }
}
