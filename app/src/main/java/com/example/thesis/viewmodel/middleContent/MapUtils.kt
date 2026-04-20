package com.example.thesis.viewmodel.middleContent

import android.content.Context
import android.util.Log
import com.arcgismaps.Color as ArcColor
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbol
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbolStyle
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import org.json.JSONObject
data class RoadDamagePoint(
    val longitude: Double,
    val latitude: Double
)

object MapUtils {

    private fun readJsonFromAssets(context: Context): JSONObject? {
        return try {
            val jsonString = context.assets
                .open("model/data/RoadDamageCoordinates.json")
                .bufferedReader()
                .use { it.readText() }
            JSONObject(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createMap(context: Context): ArcGISMap {
        val json = readJsonFromAssets(context)
        val viewpointObj = json?.getJSONObject("viewpoint")

        val latitude  = viewpointObj?.getDouble("latitude")  ?: return ArcGISMap(BasemapStyle.ArcGISStreets)
        val longitude = viewpointObj?.getDouble("longitude") ?: return ArcGISMap(BasemapStyle.ArcGISStreets)
        val scale     = viewpointObj?.getDouble("scale")     ?: return ArcGISMap(BasemapStyle.ArcGISStreets)

        return ArcGISMap(BasemapStyle.ArcGISStreets).apply {
            initialViewpoint = Viewpoint(
                latitude = latitude,
                longitude = longitude,
                scale = scale
            )
        }
    }

    fun loadCoordinatesFromAssets(context: Context): List<RoadDamagePoint> {
        val json = readJsonFromAssets(context) ?: run {
            Log.e("MapUtils", "JSON file could not be read")
            return emptyList()
        }

        return try {
            val jsonArray = json.getJSONArray("locations")
            Log.d("MapUtils", "Loaded ${jsonArray.length()} pins")

            val points = mutableListOf<RoadDamagePoint>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                points.add(
                    RoadDamagePoint(
                        longitude = obj.getDouble("longitude"),
                        latitude  = obj.getDouble("latitude")
                    )
                )
            }
            points

        } catch (e: Exception) {
            Log.e("MapUtils", "Failed to parse locations: ${e.message}")
            emptyList()
        }
    }

    fun createPointGraphic(longitude: Double, latitude: Double, color: ArcColor): Graphic {
        val point = Point(longitude, latitude, SpatialReference(wkid = 4326))
        val symbol = SimpleMarkerSymbol(SimpleMarkerSymbolStyle.Circle, color, 12f)
        return Graphic(point, symbol)
    }

    fun createGraphicsOverlayFromAssets(context: Context): GraphicsOverlay {
        val coordinates = loadCoordinatesFromAssets(context)
        val overlay = GraphicsOverlay()

        coordinates.forEach { point ->
            overlay.graphics.add(
                createPointGraphic(point.longitude, point.latitude, ArcColor.red)
            )
        }
        return overlay
    }
}