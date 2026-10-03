package com.currupt.reflame.feature.tools

import com.currupt.reflame.core.model.Tool

interface ToolRepository {
    suspend fun getTools(): List<Tool>
    suspend fun getTool(id: String): Tool?
}
