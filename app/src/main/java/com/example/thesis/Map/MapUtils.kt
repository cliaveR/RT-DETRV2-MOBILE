package com.example.thesis.Map

import com.arcgismaps.Color as ArcColor
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.symbology.*

object MapUtils {

    private val blueOutlineSymbol by lazy {
        SimpleLineSymbol(
            style = SimpleLineSymbolStyle.Solid,
            color = ArcColor.fromRgba(0, 0, 255),
            width = 2f
        )
    }
    fun createMap(): ArcGISMap {
        return ArcGISMap(BasemapStyle.ArcGISTopographic).apply {
            initialViewpoint = Viewpoint(
                latitude = 17.61866741032819,
                longitude = 120.35996878343269,
                scale = 72000.0
            )
        }
    }

    fun createPointGraphic(
        longitude: Double,
        latitude: Double,
        color: ArcColor
    ): Graphic {
        val point = Point(x = longitude, y = latitude, spatialReference = SpatialReference.wgs84())

        val simpleMarkerSymbol = SimpleMarkerSymbol(
            style = SimpleMarkerSymbolStyle.Circle,
            color = color,
            size = 10f
        ).apply {
            outline = blueOutlineSymbol
        }

        return Graphic(geometry = point, symbol = simpleMarkerSymbol)
    }
}