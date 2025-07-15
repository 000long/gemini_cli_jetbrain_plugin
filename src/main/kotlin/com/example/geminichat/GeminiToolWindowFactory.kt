package com.example.geminichat

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class GeminiToolWindowFactory : ToolWindowFactory {
    // 关键改动：createToolWindowContent 方法本身就提供了 project 对象
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // 1. 将 project 对象传递给 GeminiToolWindow 的构造函数
        val geminiToolWindow = GeminiToolWindow(toolWindow, project) 
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(geminiToolWindow.getContent(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}