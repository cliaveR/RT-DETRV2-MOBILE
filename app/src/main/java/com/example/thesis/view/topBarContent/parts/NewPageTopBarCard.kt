package com.example.thesis.view.topBarContent.parts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun NewPageTopBarCard(
    onMenuClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left - Return
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,  // .Default is alias for .Filled
                contentDescription = "Open navigation drawer",
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Right - Notifications
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.size(48.dp)
        ) {
            BadgedBox(
                badge = {
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,  // filled version looks better in top bar
                    contentDescription = "Notifications",
                    tint = Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}