package com.example.thesis.viewmodel.middleContent

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.thesis.model.data.Project
import java.time.LocalDateTime

class DamageViewModel : ViewModel() {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    init {
        loadSampleProjects()
    }

    private fun loadSampleProjects() {
        _projects.value = listOf(
            Project(
                id = "1",
                name = "Picture 1",
                lastOpened = LocalDateTime.now().minusDays(1)
            ),
            Project(
                id = "2",
                name = "Picture 2",
                lastOpened = LocalDateTime.now().minusHours(3)
            ),
            Project(
                id = "3",
                name = "Picture 3",
                lastOpened = LocalDateTime.now().minusDays(5)
            )
        ).sortedByDescending { it.lastOpened }
    }

    fun openProject(projectId: String) {
        _projects.update { currentList ->
            currentList.map {
                if (it.id == projectId) {
                    it.copy(lastOpened = LocalDateTime.now())
                } else it
            }.sortedByDescending { it.lastOpened }
        }
    }
}