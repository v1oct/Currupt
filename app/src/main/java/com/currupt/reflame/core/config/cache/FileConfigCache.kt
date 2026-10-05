package com.currupt.reflame.core.config.cache

import android.content.Context
import com.currupt.reflame.client.model.ClientConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File

class FileConfigCache(
    private val context: Context,
    private val fileName: String = CACHE_FILE_NAME,
    private val json: Json = Json { ignoreUnknownKeys = true; prettyPrint = false }
) : ConfigCache {

    private val mutex = Mutex()

    private fun getCacheFile(): File {
        return File(context.filesDir, fileName)
    }

    override suspend fun getCachedConfig(): ClientConfig? = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                val file = getCacheFile()
                if (!file.exists()) return@withContext null
                val jsonString = file.readText()
                if (jsonString.isBlank()) return@withContext null
                json.decodeFromString<ClientConfig>(jsonString)
            } catch (_: Throwable) {
                try {
                    getCacheFile().delete()
                } catch (_: Throwable) {}
                null
            }
        }
    }

    override suspend fun saveConfig(config: ClientConfig): Boolean = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                val jsonString = json.encodeToString(ClientConfig.serializer(), config)
                val tempFile = File(context.filesDir, "$fileName.tmp")
                tempFile.writeText(jsonString)
                val destFile = getCacheFile()
                if (destFile.exists()) {
                    destFile.delete()
                }
                tempFile.renameTo(destFile)
            } catch (_: Throwable) {
                false
            }
        }
    }

    override suspend fun clearCache(): Boolean = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                val file = getCacheFile()
                if (file.exists()) {
                    file.delete()
                } else {
                    true
                }
            } catch (_: Throwable) {
                false
            }
        }
    }

    companion object {
        private const val CACHE_FILE_NAME = "currupt_client_config_cache.json"
    }
}
