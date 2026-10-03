package com.currupt.reflame.core.database

import com.currupt.reflame.core.Supabase
import com.currupt.reflame.core.model.*
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Data Transfer Object for Supabase Content table.
 * Contains only the fields present in the database schema.
 */
@Serializable
data class ContentDb(
    val id: String? = null,
    val title: String,
    val slug: String,
    val description: String,
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("content_type") val contentType: String,
    val status: String,
    @SerialName("cover_url") val coverUrl: String,
    @SerialName("banner_url") val bannerUrl: String,
    @SerialName("is_featured") val isFeatured: Boolean,
    @SerialName("is_published") val isPublished: Boolean,
    val tags: List<String>,
    val metadata: JsonObject
)

/**
 * Data Transfer Object for Supabase Content Media table.
 */
@Serializable
data class MediaDb(
    val id: String? = null,
    @SerialName("content_id") val contentId: String,
    val type: String,
    val url: String,
    val title: String,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null,
    val duration: Long? = null,
    @SerialName("sort_order") val sortOrder: Int = 0
)

/**
 * Universal content repository for CURRUPT. Studio.
 */
class ContentRepository {
    private val postgrest = Supabase.client.postgrest

    suspend fun getContent(contentType: ContentType? = null, categoryId: String? = null, includeUnpublished: Boolean = false): List<Content> = withContext(Dispatchers.IO) {
        try {
            val response = postgrest["content"].select {
                filter {
                    if (!includeUnpublished) {
                        eq("is_published", true)
                    }
                    contentType?.let { eq("content_type", it.name) }
                    categoryId?.let { eq("category_id", it) }
                }
            }
            response.decodeList<Content>()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getContentBySlug(slug: String): Content? = withContext(Dispatchers.IO) {
        try {
            postgrest["content"].select {
                filter { eq("slug", slug) }
            }.decodeSingleOrNull<Content>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createContent(content: Content) = withContext(Dispatchers.IO) {
        val dbModel = content.toDb()
        postgrest["content"].insert(dbModel)
    }

    suspend fun updateContent(content: Content) = withContext(Dispatchers.IO) {
        val dbModel = content.toDb()
        postgrest["content"].update(dbModel) {
            filter { eq("id", content.id) }
        }
    }

    suspend fun deleteContent(id: String) = withContext(Dispatchers.IO) {
        postgrest["content"].delete {
            filter { eq("id", id) }
        }
    }

    private fun Content.toDb(): ContentDb {
        return ContentDb(
            id = id.ifBlank { null },
            title = title,
            slug = slug,
            description = description,
            categoryId = categoryId,
            contentType = contentType.name,
            status = status.name,
            coverUrl = coverUrl,
            bannerUrl = bannerUrl,
            isFeatured = isFeatured,
            isPublished = isPublished,
            tags = tags,
            metadata = metadata
        )
    }
}

class CategoryRepository {
    private val postgrest = Supabase.client.postgrest

    suspend fun getCategories(includeInactive: Boolean = false): List<Category> = withContext(Dispatchers.IO) {
        try {
            postgrest["categories"].select {
                filter {
                    if (!includeInactive) {
                        eq("is_active", true)
                    }
                }
            }.decodeList<Category>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createCategory(category: Category) = withContext(Dispatchers.IO) {
        postgrest["categories"].insert(category)
    }

    suspend fun updateCategory(category: Category) = withContext(Dispatchers.IO) {
        postgrest["categories"].update(category) {
            filter { eq("id", category.id) }
        }
    }

    suspend fun deleteCategory(id: String) = withContext(Dispatchers.IO) {
        postgrest["categories"].delete {
            filter { eq("id", id) }
        }
    }
}

class StudioSectionRepository {
    private val postgrest = Supabase.client.postgrest

    suspend fun getSections(includeHidden: Boolean = false): List<StudioSection> = withContext(Dispatchers.IO) {
        try {
            postgrest["studio_sections"].select {
                filter {
                    if (!includeHidden) {
                        eq("is_visible", true)
                    }
                }
            }.decodeList<StudioSection>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createSection(section: StudioSection) = withContext(Dispatchers.IO) {
        postgrest["studio_sections"].insert(section)
    }

    suspend fun updateSection(section: StudioSection) = withContext(Dispatchers.IO) {
        postgrest["studio_sections"].update(section) {
            filter { eq("id", section.id) }
        }
    }

    suspend fun deleteSection(id: String) = withContext(Dispatchers.IO) {
        postgrest["studio_sections"].delete {
            filter { eq("id", id) }
        }
    }
}

class MediaRepository {
    private val postgrest = Supabase.client.postgrest

    suspend fun getMediaForContent(contentId: String): List<MediaItem> = withContext(Dispatchers.IO) {
        try {
            postgrest["content_media"].select {
                filter { eq("content_id", contentId) }
            }.decodeList<MediaItem>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addMedia(mediaItem: MediaItem, contentId: String) = withContext(Dispatchers.IO) {
        val dbModel = MediaDb(
            contentId = contentId,
            type = mediaItem.type.name,
            url = mediaItem.url,
            title = mediaItem.title,
            thumbnailUrl = mediaItem.thumbnail,
            duration = mediaItem.duration,
            sortOrder = mediaItem.order
        )
        postgrest["content_media"].insert(dbModel)
    }

    suspend fun deleteMedia(id: String) = withContext(Dispatchers.IO) {
        postgrest["content_media"].delete {
            filter { eq("id", id) }
        }
    }
}
