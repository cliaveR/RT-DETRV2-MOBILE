package com.example.thesis.view.topContent.parts.topContent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thesis.model.enumData.NAVIGATIONPATH


@Composable
fun PictureCameraButtons(navController: NavController){
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(50)
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp,
                focusedElevation = 8.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {navController.navigate(NAVIGATIONPATH.PICTURE.route)},
                    modifier = Modifier.weight(1f).padding(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Picture")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {navController.navigate(NAVIGATIONPATH.VIDEO.route)},
                    modifier = Modifier.weight(1f).padding(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Video")
                }
            }
        }
        Spacer(modifier = Modifier.weight(2f))
    }
}