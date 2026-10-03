package com.currupt.reflame.feature.releases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currupt.reflame.core.MockData
import com.currupt.reflame.core.database.ContentRepository
import com.currupt.reflame.core.model.Content
import com.currupt.reflame.core.model.ContentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReleasesViewModel(
    private val repository: ContentRepository = ContentRepository()
) : ViewModel() {

    private val _items = MutableStateFlow<List<Content>>(emptyList())
    val items: StateFlow<List<Content>> = _items

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadReleases()
    }

    fun loadReleases() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = repository.getContent(contentType = ContentType.RELEASE)
                if (data.isEmpty()) {
                    _items.value = MockData.contents.filter { it.contentType == ContentType.RELEASE }
                } else {
                    _items.value = data
                }
            } catch (e: Exception) {
                _items.value = MockData.contents.filter { it.contentType == ContentType.RELEASE }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
