package com.currupt.reflame.feature.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.currupt.reflame.core.database.ContentRepository
import com.currupt.reflame.core.model.*
import com.currupt.reflame.feature.admin.components.EditorField
import com.currupt.reflame.feature.admin.components.filterChipColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ContentEditorViewModel(
    private val contentId: String?,
    private val repository: ContentRepository = ContentRepository()
) : ViewModel() {
    private val _content = MutableStateFlow<Content?>(null)
    val content: StateFlow<Content?> = _content

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        if (contentId != null) {
            loadContent(contentId)
        } else {
            _content.value = Content(
                id = UUID.randomUUID().toString(),
                title = "",
                slug = "",
                contentType = ContentType.PROJECT,
                status = ContentStatus.CONCEPT,
                isPublished = false
            )
        }
    }

    private fun loadContent(id: String) {
        viewModelScope.launch {
            try {
                val items = repository.getContent(includeUnpublished = true)
                _content.value = items.find { it.id == id }
            } catch (e: Exception) {
                _error.value = "Failed to load content details."
            }
        }
    }

    fun saveContent(onSuccess: () -> Unit) {
        val current = _content.value ?: return
        if (current.title.isBlank() || current.slug.isBlank()) {
            _error.value = "Title and Slug are required."
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null
            try {
                if (contentId == null) {
                    repository.createContent(current)
                } else {
                    repository.updateContent(current)
                }
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to save content. Check Supabase RLS."
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun updateTitle(title: String) {
        _content.value = _content.value?.copy(title = title)
    }

    fun updateSlug(slug: String) {
        _content.value = _content.value?.copy(slug = slug)
    }

    fun updateDescription(desc: String) {
        _content.value = _content.value?.copy(description = desc)
    }

    fun updateType(type: ContentType) {
        _content.value = _content.value?.copy(contentType = type)
    }

    fun updateStatus(status: ContentStatus) {
        _content.value = _content.value?.copy(status = status)
    }

    fun updatePublished(published: Boolean) {
        _content.value = _content.value?.copy(isPublished = published)
    }

    fun updateFeatured(featured: Boolean) {
        _content.value = _content.value?.copy(isFeatured = featured)
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(contentId: String?): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ContentEditorViewModel(contentId) as T
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ContentEditorScreen(
    contentId: String?,
    onBackClick: () -> Unit,
    viewModel: ContentEditorViewModel = viewModel(factory = ContentEditorViewModel.provideFactory(contentId))
) {
    val content by viewModel.content.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                TopAppBar(
                    title = { Text(if (contentId == null) "NEW CONTENT" else "EDIT CONTENT") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            IconButton(onClick = { viewModel.saveContent(onBackClick) }) {
                                Icon(Icons.Rounded.Save, contentDescription = "Save")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            }
        }
    ) { innerPadding ->
        if (content != null) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (error != null) {
                    Surface(
                        color = Color.Red.copy(alpha = 0.1f),
                        border = BorderStroke(1.dp, Color.Red),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error!!,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                EditorField(label = "Title", value = content!!.title, onValueChange = viewModel::updateTitle)
                EditorField(label = "Slug", value = content!!.slug, onValueChange = viewModel::updateSlug)
                EditorField(label = "Description", value = content!!.description, onValueChange = viewModel::updateDescription, singleLine = false)
                
                Text("Content Type", color = Color.White.copy(alpha = 0.4f), style = MaterialTheme.typography.labelSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ContentType.entries.filter { it != ContentType.ANNOUNCEMENT }.forEach { type ->
                        FilterChip(
                            selected = content!!.contentType == type,
                            onClick = { viewModel.updateType(type) },
                            label = { Text(type.name) },
                            colors = filterChipColors()
                        )
                    }
                }

                Text("Status", color = Color.White.copy(alpha = 0.4f), style = MaterialTheme.typography.labelSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ContentStatus.entries.forEach { status ->
                        FilterChip(
                            selected = content!!.status == status,
                            onClick = { viewModel.updateStatus(status) },
                            label = { Text(status.name) },
                            colors = filterChipColors()
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = content!!.isPublished, onCheckedChange = viewModel::updatePublished, colors = CheckboxDefaults.colors(checkedColor = Color.White))
                    Text("Published", color = Color.White)
                    Spacer(modifier = Modifier.width(24.dp))
                    Checkbox(checked = content!!.isFeatured, onCheckedChange = viewModel::updateFeatured, colors = CheckboxDefaults.colors(checkedColor = Color.White))
                    Text("Featured", color = Color.White)
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                Text("Media Management", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Architecture established in core/media. Full editor UI in next phase.", color = Color.White.copy(alpha = 0.4f), style = MaterialTheme.typography.bodySmall)
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
