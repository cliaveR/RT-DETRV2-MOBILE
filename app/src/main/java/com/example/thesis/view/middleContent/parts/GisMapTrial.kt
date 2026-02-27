@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.thesis.view.middleContent.parts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.stringResource
import com.arcgismaps.mapping.*
import com.arcgismaps.Color
import com.arcgismaps.geometry.*
import com.arcgismaps.mapping.symbology.*
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.toolkit.geoviewcompose.MapView
import com.example.thesis.R

private val blueOutlineSymbol by lazy {
    SimpleLineSymbol(SimpleLineSymbolStyle.Solid, Color.fromRgba(0, 0, 255), 2f)
}

private fun createPointGraphic(longitude: Double, latitude: Double, color: Color): Graphic { //color for custom labelling
    val point = Point(x = longitude, y = latitude, spatialReference = SpatialReference.wgs84())
    val simpleMarkerSymbol = SimpleMarkerSymbol(style = SimpleMarkerSymbolStyle.Circle, color = color, size = 10f).apply {
        outline = blueOutlineSymbol
    }
    return Graphic(geometry = point, symbol = simpleMarkerSymbol)
}

//private val pointGraphic by lazy {
    // Point(latitude, longitude, spatial reference) 16.38213959103796, 120.60478062737383
    //val point = Point( x = 120.60478062737383, y = 16.38213959103796, spatialReference = SpatialReference.wgs84())

    //val simpleMarkerSymbol = SimpleMarkerSymbol( style = SimpleMarkerSymbolStyle.Circle, color = Color.red, size = 10f)
    //simpleMarkerSymbol.outline = blueOutlineSymbol

    //Graphic(geometry = point, symbol = simpleMarkerSymbol)
//}

@Composable
fun GisMapTrial() {

    val map = remember { createMap() }

    val graphicsOverlay = remember {
        GraphicsOverlay().apply {
            graphics.addAll(
                listOf(
                    createPointGraphic(120.371082, 17.595492, Color.red),
                    createPointGraphic(120.399521, 17.581936, Color.green), //for cracks
                    createPointGraphic(120.389398, 17.589375, Color.red),

                )
            )
        }
    }

    val graphicsOverlays = remember { listOf(graphicsOverlay) }


    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) }
    ) {
        MapView(
            modifier = Modifier.fillMaxSize().padding(it), arcGISMap = map, graphicsOverlays = graphicsOverlays
        )

    }

}

fun createMap(): ArcGISMap {

    return ArcGISMap(BasemapStyle.ArcGISTopographic).apply {
        initialViewpoint = Viewpoint( latitude = 17.61866741032819, longitude = 120.35996878343269, scale = 72000.0) //17.61866741032819, 120.35996878343269

    }

}
