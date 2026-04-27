package com.example.thesis.viewmodel.middleContent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesis.domain.repository.PhotoRepository
import com.example.thesis.model.data.DamageImageItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DamageViewModel : ViewModel() {

    private val _damageImages = MutableStateFlow<List<DamageImageItem>>(emptyList())
    val damageImages: StateFlow<List<DamageImageItem>> = _damageImages

    fun loadDamageImages(repository: PhotoRepository) {
        viewModelScope.launch {
            _damageImages.value = repository.getDamageImagesFromGallery()
        }
    }

}