package com.example.thesis.view.middleContent.parts.mapContent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.example.thesis.Map.MapUtils
import com.arcgismaps.Color as ArcColor



data class RoadDamage(
    val longitude: Double,
    val latitude: Double,
    val type: String, // e.g., "Pothole", "Crack"
    val severity: Int // 1, 2, or 3
)

@Preview
@Composable
fun MapView(modifier: Modifier = Modifier){

    val map = remember { MapUtils.createMap() }

    val graphicsOverlay = remember {
        GraphicsOverlay().apply {
            graphics.addAll(
                listOf(
                    MapUtils.createPointGraphic(120.371082, 17.595492, ArcColor.red),
                    MapUtils.createPointGraphic(120.399521, 17.581936, ArcColor.green),
                    MapUtils.createPointGraphic(120.389398, 17.589375, ArcColor.red)
                )
            )
        }
    }



Box(modifier = modifier.fillMaxSize()) {
    MapView(
        modifier = Modifier.fillMaxSize(),
        arcGISMap = map,
        graphicsOverlays = listOf(graphicsOverlay)
    )
}
}