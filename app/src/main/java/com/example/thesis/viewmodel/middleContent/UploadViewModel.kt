package com.example.thesis.viewmodel.middleContent

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesis.domain.repository.PhotoRepository
import com.example.thesis.model.data.MapMarker
import com.example.thesis.model.data.mapTracking.GeoCoordinate
import com.example.thesis.model.`object`.LocalMarkerStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import kotlin.random.Random

class UploadViewModel : ViewModel() {

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress

    fun uploadImages(
        repository: PhotoRepository,
        uris: List<Uri>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            _isUploading.value = true
            val total = uris.size
            
            uris.forEachIndexed { index, uri ->
                // Generate random coordinates within Luzon, Philippines
                val randomLat = Random.nextDouble(12.5, 18.5)
                val randomLon = Random.nextDouble(119.5, 122.5)
                val randomCoord = GeoCoordinate(randomLat, randomLon)

                // Upload and save
                val result = repository.uploadAndSaveVisualized(uri, randomCoord)
                
                // Add marker to map store immediately so it shows up without restart
                if (result?.savedImageUri != null) {
                    var severity = 1
                    try {
                        result.inferenceData?.let {
                            val json = JSONObject(it)
                            val count = json.optInt("count", 0)
                            severity = when {
                                count <= 2 -> 1
                                count <= 5 -> 2
                                else -> 3
                            }
                        }
                    } catch (e: Exception) { }

                    LocalMarkerStore.addMarker(
                        MapMarker(
                            id = result.savedImageUri.lastPathSegment ?: UUID.randomUUID().toString(),
                            latitude = result.latitude ?: randomLat,
                            longitude = result.longitude ?: randomLon,
                            severity = severity,
                            damageType = "Road Damage",
                            imageUrl = result.savedImageUri.toString(),
                            capturedAt = java.time.Instant.now().toString()
                        )
                    )
                }

                _uploadProgress.value = (index + 1).toFloat() / total
            }
            
            _isUploading.value = false
            onComplete()
        }
    }
}
