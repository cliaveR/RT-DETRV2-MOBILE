package com.example.thesis.view.topBarContent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesis.R

@Composable
private fun BaseTopBar(
    navigationIcon: @Composable (() -> Unit)? = null,
    centerContent: @Composable (BoxScope.() -> Unit)? = null,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Left Slot
        Box(Modifier.align(Alignment.CenterStart)) {
            navigationIcon?.invoke()
        }

        // Center Slot
        Box(Modifier.align(Alignment.Center)) {
            centerContent?.invoke(this)
        }

        // Notification
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.align(Alignment.CenterEnd).size(48.dp)
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


@Composable
fun HomeTopBar(onMenuClick: () -> Unit, onNotify: () -> Unit) {
    BaseTopBar(
        onNotificationClick = onNotify,
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = null)
            }
        },
        centerContent = {
            Image(
                painter = painterResource(id = R.drawable.group_logo),
                contentDescription = "Logo",
                modifier = Modifier.size(42.dp)
            )
        }
    )
}

@Composable
fun ProjectTopBar(title: String, onBack: () -> Unit, onNotify: () -> Unit) {
    BaseTopBar(
        onNotificationClick = onNotify,
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = null)
            }
        },
        centerContent = {
            Text(text = title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        }
    )
}