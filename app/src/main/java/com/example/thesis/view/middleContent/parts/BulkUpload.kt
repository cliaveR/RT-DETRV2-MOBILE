package com.example.thesis.view.middleContent.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import com.example.thesis.model.middleContent.Project
import com.example.thesis.model.middleContent.ProjectImage
import com.example.thesis.model.middleContent.Upload
import com.example.thesis.model.middleContent.UploadedImage
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter



@Preview(showBackground = true)
@Composable
fun BulkUpload() {
    UploadedImageCard(
        upload = UploadedImage(
            id = "1",
            name = "Sample_Image.jpg",
            uploadDate = LocalDateTime.now()
        ),
        isSelected = false,
        onSelectChange = {},
        onEditClick = {},
        onDeleteClick = {}
    )
}
@Composable
fun UploadedImageCard(
    upload: UploadedImage,
    isSelected: Boolean,
    onSelectChange: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0E0E0)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectChange
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Default image placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFB0C0D4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = "Image placeholder",
                    tint = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Image Name: ${upload.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Upload Date: ${upload.uploadDate.format(formatter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More options"
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            expanded = false
                            onEditClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            expanded = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}