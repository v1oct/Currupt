package com.currupt.reflame.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currupt.reflame.core.auth.AdminRepository
import com.currupt.reflame.core.auth.AuthRepository
import com.currupt.reflame.core.database.CategoryRepository
import com.currupt.reflame.core.database.ContentRepository
import com.currupt.reflame.core.database.MediaRepository
import com.currupt.reflame.core.database.StudioSectionRepository
import com.currupt.reflame.core.model.Content
import com.currupt.reflame.core.model.StudioSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AdminState {
    object Loading : AdminState()
    object Unauthenticated : AdminState()
    object Unauthorized : AdminState()
    data class Dashboard(
        val totalContent: Int,
        val publishedContent: Int,
        val totalSections: Int,
        val totalMedia: Int
    ) : AdminState()
    data class Error(val message: String) : AdminState()
}

class AdminViewModel(
    private val adminRepository: AdminRepository = AdminRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
    private val contentRepository: ContentRepository = ContentRepository(),
    private val categoryRepository: CategoryRepository = CategoryRepository(),
    private val sectionRepository: StudioSectionRepository = StudioSectionRepository(),
    private val mediaRepository: MediaRepository = MediaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminState>(AdminState.Loading)
    val uiState: StateFlow<AdminState> = _uiState.asStateFlow()

    init {
        checkAuthAndLoadDashboard()
    }

    fun checkAuthAndLoadDashboard() {
        viewModelScope.launch {
            _uiState.value = AdminState.Loading
            
            if (!adminRepository.isLoggedIn()) {
                _uiState.value = AdminState.Unauthenticated
                return@launch
            }

            if (adminRepository.isCurrentUserManager()) {
                loadDashboard()
            } else {
                _uiState.value = AdminState.Unauthorized
            }
        }
    }

    private suspend fun loadDashboard() {
        try {
            val content = contentRepository.getContent(includeUnpublished = true)
            val sections = sectionRepository.getSections(includeHidden = true)
            
            _uiState.value = AdminState.Dashboard(
                totalContent = content.size,
                publishedContent = content.count { it.isPublished },
                totalSections = sections.size,
                totalMedia = 0 
            )
        } catch (e: Exception) {
            _uiState.value = AdminState.Error(e.message ?: "Failed to load dashboard")
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AdminState.Unauthenticated
            onComplete()
        }
    }
}
