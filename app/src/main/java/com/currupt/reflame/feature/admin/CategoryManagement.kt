package com.currupt.reflame.feature.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.currupt.reflame.core.database.CategoryRepository
import com.currupt.reflame.core.model.Category
import com.currupt.reflame.feature.admin.components.EditorField
import com.currupt.reflame.ui.motion.MotionSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CategoryManagementViewModel(
    private val repository: CategoryRepository = CategoryRepository()
) : ViewModel() {
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = repository.getCategories(includeInactive = true)
        }
    }

    fun saveCategory(category: Category) {
        viewModelScope.launch {
            val all = repository.getCategories(true)
            if (all.any { it.id == category.id }) {
                repository.updateCategory(category)
            } else {
                repository.createCategory(category)
            }
            loadCategories()
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            repository.deleteCategory(id)
            loadCategories()
        }
    }
}

@Composable
fun CategoryManagementScreen(
    onBackClick: () -> Unit,
    viewModel: CategoryManagementViewModel = viewModel()
) {
    val categories by viewModel.categories.collectAsState()
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        text = "CATEGORIES",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { editingCategory = Category(UUID.randomUUID().toString(), "", "", "") }) {
                        Icon(Icons.Rounded.Add, contentDescription = "Add", tint = Color.White)
                    }
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(categories) { index, category ->
                MotionSystem.ScrollReveal(index = index) {
                    CategoryRow(
                        category = category,
                        onEdit = { editingCategory = category },
                        onDelete = { showDeleteDialog = category.id }
                    )
                }
            }
        }
    }

    if (editingCategory != null) {
        CategoryEditorDialog(
            category = editingCategory!!,
            onDismiss = { editingCategory = null },
            onSave = {
                viewModel.saveCategory(it)
                editingCategory = null
            }
        )
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Category?") },
            text = { Text("This will unassign all content from this category.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog?.let { viewModel.deleteCategory(it) }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("DELETE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("CANCEL")
                }
            },
            containerColor = Color(0xFF1A1A1A),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun CategoryRow(category: Category, onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
        color = Color.White.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = category.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = category.slug, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.4f))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.4f))
            }
        }
    }
}

@Composable
fun CategoryEditorDialog(category: Category, onDismiss: () -> Unit, onSave: (Category) -> Unit) {
    var title by remember { mutableStateOf(category.title) }
    var slug by remember { mutableStateOf(category.slug) }
    var description by remember { mutableStateOf(category.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Category Editor") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                EditorField(label = "Title", value = title, onValueChange = { title = it })
                EditorField(label = "Slug", value = slug, onValueChange = { slug = it })
                EditorField(label = "Description", value = description, onValueChange = { description = it })
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(category.copy(title = title, slug = slug, description = description)) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Text("SAVE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL") }
        },
        containerColor = Color(0xFF1A1A1A),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}
