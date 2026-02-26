package com.example.thesis.view.middleContent.parts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thesis.viewmodel.middleContent.SelectedUploadViewModel

@Preview(showBackground = true)
@Composable
fun BulkUploadCard(
    uploadViewModel: SelectedUploadViewModel = viewModel()
) {

    val uploads by uploadViewModel.uploads.collectAsState()
    val selectedImages by uploadViewModel.selectedUploads.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        // Select All + Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = uploads.isNotEmpty() && selectedImages.size == uploads.size,
                onCheckedChange = { uploadViewModel.toggleSelectAll(it) }
            )

            Text(
                text = "Select All",
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { uploadViewModel.onFilterClick() }) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filter"
                )
            }
        }

        LazyColumn {
            items(uploads, key = { it.id }) { upload ->
                UploadedImageCard(
                    upload = upload,
                    isSelected = selectedImages.contains(upload.id),
                    onSelectChange = {
                        uploadViewModel.toggleUploadSelection(upload.id)
                    },
                    onEditClick = {
                        uploadViewModel.editImage(upload.id)
                    },
                    onDeleteClick = {
                        uploadViewModel.deleteUpload(upload.id)
                    }
                )
            }
        }
    }
}