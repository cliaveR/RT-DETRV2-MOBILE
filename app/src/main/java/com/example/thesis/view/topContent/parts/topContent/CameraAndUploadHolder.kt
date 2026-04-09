package com.example.thesis.view.topContent.parts.topContent

import androidx.compose.material3.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.navigation.NavController
import com.example.thesis.model.enumData.NAVIGATIONPATH

@Suppress("PreviewAnnotationInFunctionWithParameters")
@Preview
@Composable
fun CameraAndUploadHolder(navController: NavController ){

    Card(modifier = Modifier
        .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
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
                    CameraButton(navController)
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
                    UploadButton(navController)
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
