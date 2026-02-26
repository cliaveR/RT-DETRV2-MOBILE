package com.example.thesis.viewmodel.middleContent

import androidx.lifecycle.ViewModel
import com.example.thesis.model.middleContent.UploadedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalDateTime

class SelectedUploadViewModel : ViewModel() {

    // Uploads for the selected project
    private val _uploads = MutableStateFlow<List<UploadedImage>>(emptyList())
    val uploads: StateFlow<List<UploadedImage>> = _uploads

    // Selected Uploads IDs
    private val _selectedUploads = MutableStateFlow<Set<String>>(emptySet())
    val selectedUploads: StateFlow<Set<String>> = _selectedUploads

    init {
        loadSampleUploads()
    }

    // Temporary sample data (replace with DB later)
    private fun loadSampleUploads() {
        _uploads.value = listOf(
            UploadedImage(
                id = "1",
                name = "Road_Image_01.jpg",
                uploadDate = LocalDateTime.now()
            ),
            UploadedImage(
                id = "2",
                name = "Road_Image_02.jpg",
                uploadDate = LocalDateTime.now()
            ),
            UploadedImage(
                id = "3",
                name = "Road_Image_03.jpg",
                uploadDate = LocalDateTime.now()
            )
        )
    }

    fun toggleUploadSelection(uploadId: String) {
        _selectedUploads.update { current ->
            if (current.contains(uploadId)) {
                current - uploadId
            } else {
                current + uploadId
            }
        }
    }

    fun toggleSelectAll(selectAll: Boolean) {
        _selectedUploads.update {
            if (selectAll) {
                _uploads.value.map { upload -> upload.id }.toSet()
            } else {
                emptySet()
            }
        }
    }

    fun editImage(uploadId: String) {
        // TODO: edit image metadata
    }

    fun deleteUpload(uploadId: String) {
        _uploads.update { current ->
            current.filterNot { it.id == uploadId }
        }
        _selectedUploads.update { it - uploadId }
    }

    fun onFilterClick() {
        // TODO: filter logic
    }
}