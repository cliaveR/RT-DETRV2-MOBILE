package com.example.thesis.viewmodel.middleContent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesis.domain.repository.PhotoRepository
import com.example.thesis.domain.repository.VideoRepository
import com.example.thesis.model.data.DamageImageItem
import com.example.thesis.model.data.DamageVideoItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DamageViewModel : ViewModel() {

    private val _damageImages = MutableStateFlow<List<DamageImageItem>>(emptyList())
    val damageImages: StateFlow<List<DamageImageItem>> = _damageImages

    private val _damageVideos = MutableStateFlow<List<DamageVideoItem>>(emptyList())
    val damageVideos: StateFlow<List<DamageVideoItem>> = _damageVideos

    fun loadDamageImages(repository: PhotoRepository) {
        viewModelScope.launch {
            _damageImages.value = repository.getDamageImagesFromGallery()
        }
    }

    fun loadDamageVideos(repository: VideoRepository) {
        viewModelScope.launch {
            _damageVideos.value = repository.getDamageVideosFromGallery()
        }
    }

    fun loadAll(photoRepo: PhotoRepository, videoRepo: VideoRepository) {
        loadDamageImages(photoRepo)
        loadDamageVideos(videoRepo)
    }

}
