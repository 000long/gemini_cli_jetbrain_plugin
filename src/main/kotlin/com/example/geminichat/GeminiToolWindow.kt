package com.example.geminichat

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.UIUtil
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.awt.BorderLayout
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import javax.swing.JEditorPane
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.text.html.HTMLEditorKit

data class ChatTurn(val userPrompt: String, var geminiResponse: String)

class GeminiToolWindow(toolWindow: ToolWindow, private val project: Project) {

    private val outputPane = JEditorPane()
    private val promptField = com.intellij.ui.components.JBTextField()
    private val submitButton = javax.swing.JButton("Submit")
    private val panel = JPanel(BorderLayout())
    
    private val geminiApiKey = "AIzaSyDGpakhXqcLeokl_FXavDnqSiSAm4FosGI" 
    private val proxyUrl = "http://127.0.0.1:7897"
    
    private val markdownParser = Parser.builder().build()
    private val htmlRenderer = HtmlRenderer.builder().build()
    private val chatHistory = mutableListOf<ChatTurn>()

    init {
        outputPane.isEditable = false
        outputPane.contentType = "text/html"
        
        // --- 兼容旧版 SDK 的动态 CSS ---
        val editorFont = UIUtil.getLabelFont()
        // 关键改动：直接使用 getEditorPaneBackground()，它本身就是不透明的
        val editorBgColor = UIUtil.getEditorPaneBackground() 
        val textColor = UIUtil.getLabelForeground()
        
        val kit = HTMLEditorKit()
        val css = kit.styleSheet
        css.addRule("""
            body {
                font-family: ${editorFont.family}, sans-serif;
                font-size: ${editorFont.size}pt;
                color: #${Integer.toHexString(textColor.rgb and 0xffffff)};
                word-wrap: break-word;
                padding: 8px;
            }
            hr { 
                border-color: #${Integer.toHexString(UIUtil.getTooltipSeparatorColor().rgb and 0xffffff)};
                border-style: solid;
                border-width: 1px 0 0 0;
            }
            pre { 
                background-color: #${Integer.toHexString(UIUtil.getPanelBackground().rgb and 0xffffff)};
                padding: 10px; 
                border-radius: 4px; 
                border: 1px solid #${Integer.toHexString(UIUtil.getBoundsColor().rgb and 0xffffff)};
            }
            code { 
                /* 关键改动：直接使用 JetBrains Mono 字体名 */
                font-family: "JetBrains Mono", monospace; 
            }
            b {
                color: #${Integer.toHexString(UIUtil.getLabelForeground().rgb and 0xffffff)};
            }
        """.trimIndent())
        
        outputPane.editorKit = kit
        outputPane.background = editorBgColor
        // ------------------------------------
        
        val scrollPane = JBScrollPane(outputPane)
        panel.add(scrollPane, BorderLayout.CENTER)
        
        val inputPanel = JPanel(BorderLayout())
        inputPanel.add(promptField, BorderLayout.CENTER)
        inputPanel.add(submitButton, BorderLayout.EAST)
        panel.add(inputPanel, BorderLayout.SOUTH)

        promptField.addActionListener { submitButton.doClick() }
        submitButton.addActionListener {
            val prompt = promptField.text
            if (prompt.isNotBlank()) {
                promptField.text = ""
                val newTurn = ChatTurn(prompt, "...")
                chatHistory.add(newTurn)
                renderHistory()
                executeGeminiCli(newTurn)
            }
        }
    }

    private fun executeGeminiCli(currentTurn: ChatTurn) {
        val fullResponse = StringBuilder()
        
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                val command = listOf("F:\\Program Files\\nodejs\\node_global\\gemini.cmd")
                val processBuilder = ProcessBuilder(command)
                val environment = processBuilder.environment()
                environment["GEMINI_API_KEY"] = geminiApiKey
                environment["http_proxy"] = proxyUrl
                environment["https_proxy"] = proxyUrl
                project.basePath?.let { processBuilder.directory(File(it)) }
                processBuilder.redirectErrorStream(true)
                val process = processBuilder.start()

                process.outputStream.writer(StandardCharsets.UTF_8).use { it.write(currentTurn.userPrompt) }

                val reader = InputStreamReader(process.inputStream, StandardCharsets.UTF_8)
                val buffer = CharArray(128)
                var charsRead: Int
                
                while (reader.read(buffer).also { charsRead = it } != -1) {
                    if (charsRead == 0) continue
                    val chunk = String(buffer, 0, charsRead)
                    if (chunk.contains("Data collection is disabled") || chunk.contains("GEMINI_API_KEY")) continue
                    fullResponse.append(chunk)
                    currentTurn.geminiResponse = fullResponse.toString()
                    renderHistory(isStreaming = true)
                }
                
                process.waitFor()
                currentTurn.geminiResponse = fullResponse.toString()
                renderHistory()

            } catch (e: Exception) {
                SwingUtilities.invokeLater {
                    currentTurn.geminiResponse = "<p style='color:red;'>Error: ${e.message}</p>"
                    renderHistory()
                }
            }
        }
    }

    private fun renderHistory(isStreaming: Boolean = false) {
        val htmlBuilder = StringBuilder("<html><body>")
        chatHistory.forEachIndexed { index, turn ->
            htmlBuilder.append("<b>You:</b><div>${turn.userPrompt.htmlEscape()}</div><hr>")
            htmlBuilder.append("<b>Gemini:</b>")
            if (index == chatHistory.lastIndex && isStreaming) {
                htmlBuilder.append("<div>${turn.geminiResponse.htmlEscape().replace("\n", "<br>")}</div>")
            } else {
                val markdownNode = markdownParser.parse(turn.geminiResponse)
                htmlBuilder.append(htmlRenderer.render(markdownNode))
            }
        }
        htmlBuilder.append("</body></html>")
        SwingUtilities.invokeLater {
            outputPane.text = htmlBuilder.toString()
            if (!isStreaming || chatHistory.size == 1) {
                 outputPane.caretPosition = outputPane.document.length
            }
        }
    }

    private fun String.htmlEscape(): String {
        return this.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
    }

    fun getContent(): JPanel = panel
}