package com.example.thesis.view.topContent.parts.projectGISTopContent
import com.arcgismaps.Color as ArcColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.example.thesis.Map.MapUtils
import com.example.thesis.Map.MapUtils.createMap

data class RoadDamage(
    val longitude: Double,
    val latitude: Double,
    val type: String, // e.g., "Pothole", "Crack"
    val severity: Int // 1, 2, or 3
)

@Preview
@Composable
fun GISProjectDamagesCard(){

    // TEMPORARY: This list will come from the cloud in the future
    val map = remember { createMap() }
    val graphicsOverlay = remember {
        GraphicsOverlay().apply {
            graphics.addAll(
                listOf(
                    // Just call MapUtils.createPointGraphic
                    MapUtils.createPointGraphic(120.371082, 17.595492, ArcColor.red),
                    MapUtils.createPointGraphic(120.399521, 17.581936, ArcColor.green),
                    MapUtils.createPointGraphic(120.389398, 17.589375, ArcColor.red)
                )
            )
        }
    }


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ProjectGISTitle()

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F2F2)) // Match your UI's light gray
            ) {
                MapView(
                    modifier = Modifier.fillMaxSize(),
                    arcGISMap = map,
                    graphicsOverlays = listOf(graphicsOverlay)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun ProjectGISTitle(){
    Column (modifier = Modifier.padding(16.dp)){
        Text(
            text = "Project Damages",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Left,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

