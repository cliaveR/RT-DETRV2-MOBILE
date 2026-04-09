package com.example.thesis.view.popUpContent.parts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DetailBox(label: String, value:String, modifier: Modifier = Modifier){
    Card(
        modifier = modifier
            .height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(9.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style= MaterialTheme.typography.labelMedium
            )


            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE0E0E0)
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = value,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Black,
                            fontSize = 16.sp
                        ))
                }
            }
        }
    }

}

@Composable
@Preview
fun useDetailBox(){
    DetailBox("asd","asd")

}