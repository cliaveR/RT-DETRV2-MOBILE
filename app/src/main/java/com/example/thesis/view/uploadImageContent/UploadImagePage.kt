package com.example.thesis.view.uploadImageContent

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.thesis.view.uploadImageContent.parts.PhotoGallery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImagePage(navController: NavHostController) {

    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        sheetState.show()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUris.isEmpty()) {
            Text("No photos selected yet", color = Color.LightGray)
        } else {
            Text("${selectedImageUris.size} photo(s) selected", color = Color.Black)
        }
    }

    if (showSheet) {
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
            scrimColor = Color.Transparent,
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
