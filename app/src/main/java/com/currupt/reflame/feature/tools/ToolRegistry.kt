package com.currupt.reflame.feature.tools

import com.currupt.reflame.core.model.Tool

class ToolRegistry(
    private val repository: ToolRepository = LocalToolRepository()
) {
    private val dynamicRegistry = mutableMapOf<String, Tool>()

    fun registerTool(tool: Tool) {
        dynamicRegistry[tool.id] = tool
    }

    fun registerTools(tools: List<Tool>) {
        tools.forEach { registerTool(it) }
    }

    suspend fun getRegisteredTools(): List<Tool> {
        val repoTools = repository.getTools()
        val combined = (repoTools + dynamicRegistry.values).distinctBy { it.id }
        return combined.filter { it.isEnabled }
    }

    suspend fun getToolById(id: String): Tool? {
        return dynamicRegistry[id] ?: repository.getTool(id)
    }
}
