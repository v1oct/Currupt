package com.currupt.reflame.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
enum class ContentType {
    PROJECT,
    PRESET,
    TEMPLATE,
    ARTICLE,
    MEDIA,
    RELEASE,
    ANNOUNCEMENT,
    EXPERIMENT
}

@Serializable
enum class ContentStatus {
    CONCEPT,
    IN_DEVELOPMENT,
    BETA,
    RELEASED,
    ARCHIVED
}

@Serializable
enum class MediaType {
    IMAGE,
    VIDEO,
    AUDIO,
    GALLERY,
    EXTERNAL_LINK
}

@Serializable
data class Category(
    val id: String,
    val title: String,
    val slug: String,
    val description: String = "",
    @SerialName("is_active") val isActive: Boolean = true
)

@Serializable
data class MediaItem(
    val id: String,
    val type: MediaType,
    val url: String,
    val title: String = "",
    val thumbnail: String? = null,
    val duration: Long? = null, // in milliseconds
    val order: Int = 0
)

@Serializable
data class Content(
    val id: String,
    val title: String,
    val slug: String,
    val description: String = "",
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("content_type") val contentType: ContentType,
    val status: ContentStatus = ContentStatus.RELEASED,
    @SerialName("cover_url") val coverUrl: String = "",
    @SerialName("banner_url") val bannerUrl: String = "",
    @SerialName("is_featured") val isFeatured: Boolean = false,
    @SerialName("is_published") val isPublished: Boolean = true,
    val tags: List<String> = emptyList(),
    val metadata: JsonObject = JsonObject(emptyMap()),
    val media: List<MediaItem> = emptyList(),
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
enum class SectionType {
    HERO,
    RAIL,
    GRID,
    FEATURED,
    ANNOUNCEMENT,
    MEDIA,
    TEXT
}

@Serializable
data class StudioSection(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val type: SectionType,
    val priority: Int = 0,
    @SerialName("is_visible") val isVisible: Boolean = true,
    @SerialName("query_config") val queryConfig: JsonObject = JsonObject(emptyMap())
)
