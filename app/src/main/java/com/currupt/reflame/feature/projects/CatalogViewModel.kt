package com.currupt.reflame.feature.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currupt.reflame.core.MockData
import com.currupt.reflame.core.database.ContentRepository
import com.currupt.reflame.core.model.Content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val repository: ContentRepository = ContentRepository()
) : ViewModel() {

    private val _items = MutableStateFlow<List<Content>>(emptyList())
    val items: StateFlow<List<Content>> = _items

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadCatalog()
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = repository.getContent()
                if (data.isEmpty()) {
                    // Fallback to MockData only if DB is genuinely empty
                    _items.value = MockData.contents
                } else {
                    _items.value = data
                }
            } catch (e: Exception) {
                // If fetch fails (RLS or Network), show MockData for dev
                _items.value = MockData.contents
            } finally {
                _isLoading.value = false
            }
        }
    }
}
