package com.example.thesis.view.slideUp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SlideUpCardFilter(
    onDismiss: () -> Unit
) {
    var project1 by remember { mutableStateOf(false) }
    var damage1 by remember { mutableStateOf(false) }
    var projectDamage by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filters") },
        text = {
            Column {

                FilterItem(
                    label = "Project 1",
                    checked = project1,
                    onCheckedChange = { project1 = it }
                )

                FilterItem(
                    label = "Damage 1",
                    checked = damage1,
                    onCheckedChange = { damage1 = it }
                )

                FilterItem(
                    label = "Projectdamage",
                    checked = projectDamage,
                    onCheckedChange = { projectDamage = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Apply")
            }
        }
    )
}

@Composable
private fun FilterItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}