package com.example.thesis.view.topContent.parts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun CameraCard(){

    Card(modifier = Modifier
        .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    )
    {
        TittleText()

        Column (modifier = Modifier
            .padding(bottom = 24.dp)
            .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Spacer(modifier = Modifier.size(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CameraButton()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Camera",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UploadButton()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Upload",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

    }
}

@Composable
fun CameraButton (){
    Box(
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background((Color.LightGray)),
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "Upload photo",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
@Composable
fun UploadButton (){
    Box(
        contentAlignment = Alignment.Center
    ){
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background((Color.LightGray)),
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "Upload photo",
                tint = Color.Black,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun TittleText(){
    Column (modifier = Modifier.padding(16.dp)){
        Text(
            text = "Road Damage Detection",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
