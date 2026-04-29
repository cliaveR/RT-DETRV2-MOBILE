package com.example.thesis.domain.repository

import android.util.Log
import com.example.thesis.model.data.MapMarker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class MapMarkerRepository(
    private val baseUrl: String = "http://192.168.254.200:8080"
) {
    private val tag = "MAP_MARKER_REPO"
    private val client = OkHttpClient()

    suspend fun createMarker(marker: MapMarker): Boolean = withContext(Dispatchers.IO) {
        try {
            val payload = JSONObject().apply {
                put("id", marker.id)
                put("longitude", marker.longitude)
                put("latitude", marker.latitude)
                put("severity", marker.severity)
                put("damageType", marker.damageType)
                put("imageUrl", marker.imageUrl)
                put("capturedAt", marker.capturedAt)
            }

            val request = Request.Builder()
                .url("$baseUrl/api/markers")
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.w(tag, "createMarker failed: ${response.code}")
                }
                response.isSuccessful
            }
        } catch (e: Exception) {
            Log.e(tag, "createMarker exception: ${e.message}")
            false
        }
    }

    suspend fun getMarkers(): List<MapMarker> = withContext(Dispatchers.IO) {
        val endpoints = listOf(
            "$baseUrl/api/markers",
            "$baseUrl/api/upload/markers"
        )

        for (url in endpoints) {
            try {
                val request = Request.Builder().url(url).get().build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.w(tag, "getMarkers failed on $url: ${response.code}")
                        return@use
                    }

                    val body = response.body?.string().orEmpty()
                    if (body.isBlank()) return@withContext emptyList()
                    return@withContext parseMarkerResponse(body)
                }
            } catch (e: Exception) {
                Log.w(tag, "getMarkers exception on $url: ${e.message}")
            }
        }

        emptyList()
    }

    private fun parseMarkerResponse(rawJson: String): List<MapMarker> {
        val trimmed = rawJson.trim()
        val array = when {
            trimmed.startsWith("[") -> JSONArray(trimmed)
            trimmed.startsWith("{") -> {
                val root = JSONObject(trimmed)
                when {
                    root.has("markers") -> root.optJSONArray("markers") ?: JSONArray()
                    root.has("data") -> root.optJSONArray("data") ?: JSONArray()
                    else -> JSONArray()
                }
            }
            else -> JSONArray()
        }

        val parsed = mutableListOf<MapMarker>()
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val longitude = item.optDouble("longitude", item.optDouble("lng", Double.NaN))
            val latitude = item.optDouble("latitude", item.optDouble("lat", Double.NaN))
            if (longitude.isNaN() || latitude.isNaN()) continue

            parsed.add(
                MapMarker(
                    id = item.optString("id", "marker_$i"),
                    longitude = longitude,
                    latitude = latitude,
                    severity = item.optInt("severity", 2),
                    damageType = item.optString("damageType", item.optString("type", "Unknown")),
                    imageUrl = item.optString("imageUrl", item.optString("photoUrl", "")).ifBlank { null },
                    capturedAt = item.optString("capturedAt", item.optString("createdAt", "")).ifBlank { null }
                )
            )
        }

        return parsed
    }
}



