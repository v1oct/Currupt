package com.currupt.reflame.feature.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
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
import com.currupt.reflame.core.database.StudioSectionRepository
import com.currupt.reflame.core.model.SectionType
import com.currupt.reflame.core.model.StudioSection
import com.currupt.reflame.feature.admin.components.EditorField
import com.currupt.reflame.feature.admin.components.filterChipColors
import com.currupt.reflame.ui.motion.MotionSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CanvasManagementViewModel(
    private val repository: StudioSectionRepository = StudioSectionRepository()
) : ViewModel() {
    private val _sections = MutableStateFlow<List<StudioSection>>(emptyList())
    val sections: StateFlow<List<StudioSection>> = _sections

    init {
        loadSections()
    }

    fun loadSections() {
        viewModelScope.launch {
            _sections.value = repository.getSections(includeHidden = true).sortedBy { it.priority }
        }
    }

    fun saveSection(section: StudioSection) {
        viewModelScope.launch {
            val all = repository.getSections(true)
            if (all.any { it.id == section.id }) {
                repository.updateSection(section)
            } else {
                repository.createSection(section)
            }
            loadSections()
        }
    }

    fun deleteSection(id: String) {
        viewModelScope.launch {
            repository.deleteSection(id)
            loadSections()
        }
    }
}

@Composable
fun CanvasManagementScreen(
    onBackClick: () -> Unit,
    viewModel: CanvasManagementViewModel = viewModel()
) {
    val sections by viewModel.sections.collectAsState()
    var editingSection by remember { mutableStateOf<StudioSection?>(null) }
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
                        text = "HOME CANVAS",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { editingSection = StudioSection(UUID.randomUUID().toString(), "", "", SectionType.RAIL, sections.size) }) {
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
            itemsIndexed(sections) { index, section ->
                MotionSystem.ScrollReveal(index = index) {
                    SectionRow(
                        section = section,
                        onEdit = { editingSection = section },
                        onDelete = { showDeleteDialog = section.id }
                    )
                }
            }
        }
    }

    if (editingSection != null) {
        SectionEditorDialog(
            section = editingSection!!,
            onDismiss = { editingSection = null },
            onSave = {
                viewModel.saveSection(it)
                editingSection = null
            }
        )
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Remove Section?") },
            text = { Text("This will remove the section from the public homepage.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog?.let { viewModel.deleteSection(it) }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("REMOVE")
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
fun SectionRow(section: StudioSection, onEdit: () -> Unit, onDelete: () -> Unit) {
    Surface(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
        color = Color.White.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = section.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = "${section.type.name} · Priority ${section.priority}", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f))
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Rounded.Settings, contentDescription = "Edit", tint = Color.White.copy(alpha = 0.4f))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.4f))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SectionEditorDialog(section: StudioSection, onDismiss: () -> Unit, onSave: (StudioSection) -> Unit) {
    var title by remember { mutableStateOf(section.title) }
    var subtitle by remember { mutableStateOf(section.subtitle) }
    var type by remember { mutableStateOf(section.type) }
    var priority by remember { mutableIntStateOf(section.priority) }
    var isVisible by remember { mutableStateOf(section.isVisible) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Section Editor") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                EditorField(label = "Title", value = title, onValueChange = { title = it })
                EditorField(label = "Subtitle", value = subtitle, onValueChange = { subtitle = it })
                
                Text("Type", color = Color.White.copy(alpha = 0.4f), style = MaterialTheme.typography.labelSmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionType.entries.forEach { sectionType ->
                        FilterChip(
                            selected = type == sectionType,
                            onClick = { type = sectionType },
                            label = { Text(sectionType.name) },
                            colors = filterChipColors()
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isVisible, onCheckedChange = { isVisible = it }, colors = CheckboxDefaults.colors(checkedColor = Color.White))
                    Text("Visible on Home", color = Color.White)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(section.copy(title = title, subtitle = subtitle, type = type, priority = priority, isVisible = isVisible)) },
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
