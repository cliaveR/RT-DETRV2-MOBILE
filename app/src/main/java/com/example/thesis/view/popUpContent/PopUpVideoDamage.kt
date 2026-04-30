package com.example.thesis.view.popUpContent

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.thesis.domain.repository.VideoRepository
import com.example.thesis.model.data.DamageVideoItem
import com.example.thesis.view.popUpContent.parts.CoordinateRow
import com.example.thesis.view.popUpContent.parts.convertDecimalToDMS
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PopUpVideoDamage(videoUri: Uri?, navController: NavController) {
    val context = LocalContext.current
    val repository = remember { VideoRepository(context, "") }
    var videoItem by remember { mutableStateOf<DamageVideoItem?>(null) }

    LaunchedEffect(videoUri) {
        if (videoUri != null) {
            videoItem = repository.getVideoDetails(videoUri)
        }
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            videoUri?.let {
                setMediaItem(MediaItem.fromUri(it))
                prepare()
                playWhenReady = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(bottom = 16.dp)
                .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
        ) {
            Icon(Icons.Default.ArrowBackIosNew, "Back", tint = Color.White)
        }

        // Video Player
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Video Analysis Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DetailRow("File Name", videoItem?.displayName ?: "N/A")
                
                val formattedDate = videoItem?.dateAddedSeconds?.let {
                    Instant.ofEpochSecond(it)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a"))
                } ?: "N/A"
                DetailRow("Captured At", formattedDate)

                if (videoItem?.processingTimeMs != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF0F7FF), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI Processing Time:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "${videoItem?.processingTimeMs} ms",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0084FF)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Coordinate Table Section
                Text(
                    text = "LOCATION DATA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

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

                CoordinateRow(
                    label = "Latitude", 
                    decimalValue = videoItem?.latitude?.let { "%.6f".format(it) } ?: "N/A", 
                    dmsValue = convertDecimalToDMS(videoItem?.latitude, true)
                )

                Spacer(modifier = Modifier.height(12.dp))

                CoordinateRow(
                    label = "Longitude", 
                    decimalValue = videoItem?.longitude?.let { "%.6f".format(it) } ?: "N/A", 
                    dmsValue = convertDecimalToDMS(videoItem?.longitude, false)
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Black)
    }
}
