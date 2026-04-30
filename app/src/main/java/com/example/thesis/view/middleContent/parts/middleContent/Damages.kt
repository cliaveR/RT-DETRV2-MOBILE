package com.example.thesis.view.middleContent.parts.middleContent

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.thesis.domain.repository.PhotoRepository
import com.example.thesis.domain.repository.VideoRepository
import com.example.thesis.model.enumData.NAVIGATIONPATH
import com.example.thesis.viewmodel.middleContent.DamageViewModel

@Composable
fun Damages(
    navController: NavController,
    damageViewModel: DamageViewModel = viewModel()
) {
    val context = LocalContext.current
    val photoRepo = remember { PhotoRepository(context) }
    val videoRepo = remember { VideoRepository(context, "http://192.168.254.201:8080/api/upload/video") }

    val damageImages by damageViewModel.damageImages.collectAsState()
    val damageVideos by damageViewModel.damageVideos.collectAsState()

    LaunchedEffect(Unit) {
        damageViewModel.loadAll(photoRepo, videoRepo)
    }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Damage Detection History",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, top = 8.dp)
        )

        if (damageImages.isEmpty() && damageVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Detections Yet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Section for Videos
                if (damageVideos.isNotEmpty()) {
                    item {
                        Text(
                            "Videos",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(damageVideos) { video ->
                        DamageVideoCard(
                            video = video,
                            onClick = {
                                navController.navigate(
                                    "${NAVIGATIONPATH.VIDEO_DAMAGE.route}/${Uri.encode(video.uri.toString())}"
                                )
                            }
                        )
                    }
                }

                // Section for Images
                if (damageImages.isNotEmpty()) {
                    item {
                        Text(
                            "Images",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }
                    items(damageImages) { image ->
                        DamageCard(
                            image = image,
                            onClick = {
                                navController.navigate(
                                    "${NAVIGATIONPATH.DAMAGE.route}/${Uri.encode(image.uri.toString())}"
                                )
                            },
                            onEditClick = {},
                            onDeleteClick = {}
                        )
                    }
                }
            }
        }
    }
}
