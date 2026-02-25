package com.example.thesis.view.topBarContent.parts

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thesis.R
@Preview
@Composable
fun MenuCard(
    onMenuClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)           // standard top bar height
            .background(Color.White)  // very dark / almost black - change as needed
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Left - Menu / Hamburger
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Open menu",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.group_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Right - Notification bell
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.size(48.dp)
        ) {
            BadgedBox(
                badge = {
                    // Optional: show badge if there are unread notifications
                    // Badge { Text("3") }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}