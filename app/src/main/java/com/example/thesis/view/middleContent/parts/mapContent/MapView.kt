package com.example.thesis.view.middleContent.parts.mapContent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.example.thesis.viewmodel.middleContent.MapUtils
import com.arcgismaps.Color as ArcColor

@Preview
@Composable
fun MapViewScreen(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val map = remember { MapUtils.createMap(context) }

    val graphicsOverlay = remember {
        MapUtils.createGraphicsOverlayFromAssets(context)
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapView(
            modifier = Modifier.fillMaxSize(),
            arcGISMap = map,
            graphicsOverlays = listOf(graphicsOverlay)
        )
    }
}