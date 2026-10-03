package com.currupt.reflame.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currupt.reflame.core.MockData
import com.currupt.reflame.core.database.ContentRepository
import com.currupt.reflame.core.database.StudioSectionRepository
import com.currupt.reflame.core.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(
        val sections: List<HomeSectionData>
    ) : HomeState()
    data class Error(val message: String) : HomeState()
}

data class HomeSectionData(
    val section: StudioSection,
    val items: List<Content>
)

class HomeViewModel(
    private val contentRepository: ContentRepository = ContentRepository(),
    private val sectionRepository: StudioSectionRepository = StudioSectionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState: StateFlow<HomeState> = _uiState

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = HomeState.Loading
            try {
                // Fetch sections first
                val sections = sectionRepository.getSections().sortedBy { it.priority }
                
                if (sections.isEmpty()) {
                    loadMockHome()
                    return@launch
                }

                // Fetch all content once to avoid multiple DB calls in a loop
                val allContent = contentRepository.getContent()

                val homeSections = sections.map { section ->
                    val items = when (section.type) {
                        SectionType.HERO, SectionType.FEATURED -> allContent.filter { it.isFeatured }
                        SectionType.ANNOUNCEMENT -> allContent.filter { it.contentType == ContentType.ANNOUNCEMENT }
                        SectionType.RAIL -> {
                            if (section.title.contains("Experiments", ignoreCase = true)) {
                                allContent.filter { it.contentType == ContentType.EXPERIMENT }
                            } else {
                                allContent.filter { it.contentType == ContentType.PROJECT }
                            }
                        }
                        else -> allContent
                    }
                    HomeSectionData(section, items)
                }
                
                // If everything is empty (even after joined filtering), fallback
                if (homeSections.all { it.items.isEmpty() && it.section.type != SectionType.TEXT }) {
                    loadMockHome()
                } else {
                    _uiState.value = HomeState.Success(homeSections)
                }
            } catch (e: Exception) {
                // If network error, fallback to MockData
                loadMockHome()
            }
        }
    }

    private fun loadMockHome() {
        val mockHomeSections = MockData.sections.map { section ->
            val items = when (section.type) {
                SectionType.FEATURED, SectionType.HERO -> MockData.contents.filter { it.isFeatured }
                SectionType.ANNOUNCEMENT -> MockData.contents.filter { it.contentType == ContentType.ANNOUNCEMENT }
                SectionType.RAIL -> {
                    if (section.title.contains("Experiments", ignoreCase = true)) {
                        MockData.contents.filter { it.contentType == ContentType.EXPERIMENT }
                    } else {
                        MockData.contents.filter { it.contentType == ContentType.PROJECT }
                    }
                }
                else -> MockData.contents
            }
            HomeSectionData(section, items)
        }
        _uiState.value = HomeState.Success(mockHomeSections)
    }
}
