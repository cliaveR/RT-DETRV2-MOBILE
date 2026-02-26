package com.example.thesis.viewmodel.middleContent

import androidx.lifecycle.ViewModel
import com.example.thesis.model.middleContent.ProjectImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class SelectedProjectViewModel : ViewModel() {

    // Images for the selected project
    private val _images = MutableStateFlow<List<ProjectImage>>(emptyList())
    val images: StateFlow<List<ProjectImage>> = _images

    // Selected image IDs
    private val _selectedImages = MutableStateFlow<Set<String>>(emptySet())
    val selectedImages: StateFlow<Set<String>> = _selectedImages

    init {
        loadSampleImages()
    }

    // Temporary sample data (replace with DB later)
    private fun loadSampleImages() {
        _images.value = listOf(
            ProjectImage(
                id = "1",
                name = "Road_Image_01.jpg",
                uploadDate = LocalDate.now().minusDays(1)
            ),
            ProjectImage(
                id = "2",
                name = "Road_Image_02.jpg",
                uploadDate = LocalDate.now().minusDays(2)
            ),
            ProjectImage(
                id = "3",
                name = "Road_Image_03.jpg",
                uploadDate = LocalDate.now().minusDays(4)
            )
        )
    }

    fun toggleImageSelection(imageId: String) {
        _selectedImages.update { current ->
            if (current.contains(imageId)) {
                current - imageId
            } else {
                current + imageId
            }
        }
    }

    fun toggleSelectAll(selectAll: Boolean) {
        _selectedImages.update {
            if (selectAll) {
                _images.value.map { image -> image.id }.toSet()
            } else {
                emptySet()
            }
        }
    }

    fun editImage(imageId: String) {
        // TODO: edit image metadata
    }

    fun deleteImage(imageId: String) {
        _images.update { current ->
            current.filterNot { it.id == imageId }
        }
        _selectedImages.update { it - imageId }
    }

    fun onFilterClick() {
        // TODO: filter logic
    }
}