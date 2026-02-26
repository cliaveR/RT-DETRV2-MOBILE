package com.example.thesis.viewmodel.middleContent

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.thesis.model.middleContent.Project
import com.example.thesis.model.middleContent.Upload
import java.time.LocalDateTime

class UploadViewModel : ViewModel() {

    private val _uploads = MutableStateFlow<List<Upload>>(emptyList())
    val Upload: StateFlow<List<Upload>> = _uploads

    init {
        loadSampleUpload()
    }

    private fun loadSampleUpload() {
        _uploads.value = listOf(
            Upload(
                id = "1",
                fileName = "Image_001",
                uploadDate = LocalDateTime.now()
            ),
            Upload(
                id = "2",
                fileName = "Image_002",
                uploadDate = LocalDateTime.now()
            ),
            Upload(
                id = "3",
                fileName = "Image_003",
                uploadDate = LocalDateTime.now()
            )
        ).sortedByDescending { it.uploadDate }
    }

    fun openUpload(UploadId: String) {
        _uploads.update { currentList ->
            currentList.map {
                if (it.id == UploadId) {
                    it.copy(uploadDate = LocalDateTime.now())
                } else it
            }.sortedByDescending { it.uploadDate }
        }
    }
}