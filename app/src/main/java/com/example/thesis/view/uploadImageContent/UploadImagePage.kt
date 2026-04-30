package com.example.thesis.view.uploadImageContent

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.thesis.domain.repository.PhotoRepository
import com.example.thesis.view.uploadImageContent.parts.PhotoGallery
import com.example.thesis.viewmodel.middleContent.UploadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImagePage(
    navController: NavHostController,
    uploadViewModel: UploadViewModel = viewModel()
) {
    val context = LocalContext.current
    val repository = remember { PhotoRepository(context) }

    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(true) }

    val isUploading by uploadViewModel.isUploading.collectAsState()
    val progress by uploadViewModel.uploadProgress.collectAsState()

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (selectedImageUris.isEmpty()) {
                Text("No photos selected yet", color = Color.LightGray)
            } else {
                Text(
                    text = "${selectedImageUris.size} photo(s) selected",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isUploading) {
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.size(64.dp),
                        color = Color(0xFF0084FF)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Processing... ${(progress * 100).toInt()}%",
                        color = Color.Gray
                    )
                } else {
                    Button(
                        onClick = {
                            uploadViewModel.uploadImages(repository, selectedImageUris) {
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0084FF))
                    ) {
                        Text("Upload and Process", fontSize = 16.sp)
                    }

                    TextButton(onClick = { showSheet = true }) {
                        Text("Change Selection", color = Color.Gray)
                    }
                }
            }
        }
    }

    if (showSheet && !isUploading) {
        ModalBottomSheet(
            onDismissRequest = {
                if (selectedImageUris.isEmpty()) {
                    navController.popBackStack()
                } else {
                    showSheet = false
                }
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            containerColor = Color.White,
            contentWindowInsets = { WindowInsets(0) },
            scrimColor = Color.Black.copy(alpha = 0.32f),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDEDEDE))
                )
            }
        ) {
            PhotoGallery(
                onImagesSelected = { uris ->
                    selectedImageUris = uris
                    showSheet = false
                },
                onClose = {
                    showSheet = false
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ImagePreview() {
    val navController = rememberNavController()
    UploadImagePage(navController = navController)
}
