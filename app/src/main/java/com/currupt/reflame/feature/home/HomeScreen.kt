package com.currupt.reflame.feature.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.currupt.reflame.core.model.*
import com.currupt.reflame.ui.motion.MotionSystem
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun HomeScreen(
    onContentClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is HomeState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
            }
            is HomeState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Failed to load home. ${state.message}", color = Color.White.copy(alpha = 0.5f))
                }
            }
            is HomeState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = 100.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    state.sections.forEachIndexed { index, sectionData ->
                        MotionSystem.ScrollReveal(index = index) {
                            SectionRenderer(
                                section = sectionData.section,
                                items = sectionData.items,
                                onContentClick = onContentClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionRenderer(
    section: StudioSection,
    items: List<Content>,
    onContentClick: (String) -> Unit
) {
    if (items.isEmpty() && section.type != SectionType.TEXT) return

    when (section.type) {
        SectionType.HERO -> {
            HeroCarousel(
                contents = items.filter { it.isFeatured },
                onContentClick = onContentClick
            )
        }
        SectionType.ANNOUNCEMENT -> {
            items.firstOrNull()?.let {
                AnnouncementBoard(announcement = it)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        SectionType.RAIL -> {
            ContentSectionRail(
                title = section.title,
                subtitle = section.subtitle,
                items = items,
                onContentClick = onContentClick
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
        SectionType.GRID -> {
            // Placeholder
        }
        SectionType.TEXT -> {
            TextSection(title = section.title, content = section.subtitle)
            Spacer(modifier = Modifier.height(24.dp))
        }
        else -> {}
    }
}

@Composable
fun AnnouncementBoard(announcement: Content) {
    Surface(
        onClick = { /* Action */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(16.dp)),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "ANNOUNCEMENT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                )
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun HeroCarousel(
    contents: List<Content>,
    onContentClick: (String) -> Unit
) {
    if (contents.isEmpty()) return
    
    var currentIndex by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(5.seconds)
            currentIndex = (currentIndex + 1) % contents.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF121212))
    ) {
        // Hero Content with Crossfade
        AnimatedContent(
            targetState = contents[currentIndex],
            transitionSpec = {
                fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(animationSpec = tween(1000))
            },
            label = "hero_fade"
        ) { item ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onContentClick(item.slug) }
            ) {
                AsyncImage(
                    model = item.bannerUrl.ifBlank { item.coverUrl },
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Readability Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                startY = 300f
                            )
                        )
                )
                
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    ) {
                        Text(
                            text = item.status.name.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )
                    
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.6f)
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { onContentClick(item.slug) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text("VIEW PROJECT", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        // Indicators
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
        ) {
            contents.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(if (index == currentIndex) 12.dp else 6.dp)
                        .background(
                            if (index == currentIndex) Color.White else Color.White.copy(alpha = 0.3f),
                            CircleShape
                        )
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun ContentSectionRail(
    title: String,
    subtitle: String?,
    items: List<Content>,
    onContentClick: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    )
                }
            }
            Text(
                text = "SEE ALL",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.5f)
                ),
                modifier = Modifier.clickable { /* Action */ }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(items) { index, item ->
                MotionSystem.ScrollReveal(index = index) {
                    ContentCard(content = item, onClick = { onContentClick(item.slug) })
                }
            }
        }
    }
}

@Composable
fun ContentCard(
    content: Content,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.width(160.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1A1A))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = content.coverUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Progress placeholder if appropriate
                if (content.status == ContentStatus.IN_DEVELOPMENT) {
                    LinearProgressIndicator(
                        progress = { 0.5f }, // From metadata later
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .align(Alignment.BottomCenter),
                        color = Color.White,
                        trackColor = Color.Transparent
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = content.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = content.contentType.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.4f)
                )
            )
        }
    }
}

@Composable
fun TextSection(title: String, content: String) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = content, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
    }
}
