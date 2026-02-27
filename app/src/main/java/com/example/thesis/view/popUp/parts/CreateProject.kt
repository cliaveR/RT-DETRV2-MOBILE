package com.example.thesis.view.popUp.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateProjectPopUp(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Dimmed background overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f) //
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE6E6E6)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Create a Project",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Section (Name + Image)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Project Name Input
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Project Name", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        AdaptiveTextField(
                            value = projectName,
                            onValueChange = { projectName = it },
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        )
                    }

                    // Image Upload Placeholder
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Image", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(50.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFD9D9D9)),
                            onClick = { /* Handle image upload */ }
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.FileUpload, contentDescription = null, tint = Color.DarkGray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description Section
                Text(text = "Description", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                AdaptiveTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 200.dp) // Grows with screen height
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Button (Functional)
                Button(
                    onClick = { onCreate(projectName, description) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB0C0D4) // Soft blue-grey from your theme
                    )
                ) {
                    Text(
                        text = "Create",
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFD9D9D9),
            unfocusedContainerColor = Color(0xFFD9D9D9),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateProject() {
    CreateProjectPopUp(onDismiss = {}, onCreate = { _, _ -> })
}