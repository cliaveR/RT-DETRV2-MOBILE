package com.example.thesis.viewmodel.middleContent

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesis.domain.repository.PhotoRepository
import com.example.thesis.model.data.mapTracking.GeoCoordinate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
                // Luzon approx bounding box: Lat (12.5 to 18.5), Lon (119.5 to 122.5)
                val randomLat = Random.nextDouble(12.5, 18.5)
                val randomLon = Random.nextDouble(119.5, 122.5)
                val randomCoord = GeoCoordinate(randomLat, randomLon)

                // Pass the random coordinate to the repository
                repository.uploadAndSaveVisualized(uri, randomCoord)

                _uploadProgress.value = (index + 1).toFloat() / total
            }
            
            _isUploading.value = false
            onComplete()
        }
    }
}
