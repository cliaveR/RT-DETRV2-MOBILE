package com.example.thesis.view.appNavigation.sideBarContent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun SidebarContent(
    onGoMain: () -> Unit,
    onGoMap: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.group_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(40.dp))
        NavigationItem(label = "Main page", onClick = onGoMain)
        NavigationItem(label = "Map page", onClick = onGoMap)
        NavigationItem(label = "Close sidebar", onClick = onClose)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Copyright © 2026",
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
fun NavigationItem(label: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Text(text = label, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}