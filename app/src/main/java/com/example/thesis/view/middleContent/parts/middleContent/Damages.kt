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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.thesis.domain.repository.PhotoRepository
import com.example.thesis.model.enumData.NAVIGATIONPATH
import com.example.thesis.viewmodel.middleContent.DamageViewModel

@Composable
fun Damages(
    navController: NavController,
    damageViewModel: DamageViewModel = viewModel()
) {


    val context = LocalContext.current
    val repository = remember { PhotoRepository(context) }

    val projects by damageViewModel.projects.collectAsState()
    val latestImage by damageViewModel.latestImage.collectAsState()

    LaunchedEffect(Unit) {
        damageViewModel.loadLatestImage(repository)
    }

    Column(
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )

    ) {

        Text(
            text = "Projects",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)

        )
        if (projects.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No Projects",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray, // Gray looks better for empty states
                    textAlign = TextAlign.Center
                )
            }
        } else {

            LazyColumn {
                items(projects) { project ->
                    DamageCard(
                        project = project,
                        imageUri = latestImage,
                        onClick = {
                            latestImage?.let {
                                navController.navigate(
                                    "${NAVIGATIONPATH.DAMAGE.route}/${Uri.encode(it.toString())}"
                                )
                            }
                        },
                        onEditClick = {},
                        onDeleteClick = {}
                    )
                }
            }
        }
    }
}