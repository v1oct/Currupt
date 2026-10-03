package com.currupt.reflame.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GameProfile(
    @SerialName("game_id") val gameId: String,
    @SerialName("enabled_tools") val enabledTools: List<String> = emptyList(),
    @SerialName("configuration_values") val configurationValues: JsonObject = JsonObject(emptyMap()),
    @SerialName("display_metadata") val displayMetadata: JsonObject = JsonObject(emptyMap())
)
