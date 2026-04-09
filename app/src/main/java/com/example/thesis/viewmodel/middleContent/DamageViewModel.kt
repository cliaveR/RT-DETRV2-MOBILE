package com.example.thesis.viewmodel.middleContent

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesis.domain.repository.PhotoRepository
import com.example.thesis.model.data.Project
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class DamageViewModel : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    private val _latestImage = MutableStateFlow<Uri?>(null)
    val latestImage: StateFlow<Uri?> = _latestImage

    init {
        loadSampleProjects()
    }

    fun loadLatestImage(repository: PhotoRepository) {
        viewModelScope.launch {
            _latestImage.value = repository.getLatestImageFromGallery()
        }
    }

    private fun loadSampleProjects() {
        _projects.value = listOf(
            Project("1", "Picture 1", LocalDateTime.now().minusDays(1)),
            Project("2", "Picture 2", LocalDateTime.now().minusHours(3)),
            Project("3", "Picture 3", LocalDateTime.now().minusDays(5))
        ).sortedByDescending { it.lastOpened }
    }

    fun openProject(projectId: String) {
        _projects.update { list ->
            list.map {
                if (it.id == projectId) {
                    it.copy(lastOpened = LocalDateTime.now())
                } else it
            }.sortedByDescending { it.lastOpened }
        }
    }
}