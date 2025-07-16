 # Gemini Chat for PyCharm

这是一个为 PyCharm 开发的非官方插件，旨在提供一个与 Google Gemini 模型进行交互的便捷聊天窗口。
它通过调用本地安装的 `gemini` 命令行工具来工作，并将结果以美观、实时的 Markdown 格式展示在 PyCharm 的工具窗口中。

![Plugin Screenshot](https://raw.githubusercontent.com/your-username/your-repo/main/docs/screenshot.png) 
*(请将此处的图片链接替换为您自己的截图链接)*

---

## ✨ 功能特性

*   **实时流式输出**：像在真实的聊天应用中一样，实时看到 Gemini 的回答。
*   **Markdown 渲染**：完美支持代码块、列表、加粗等 Markdown 格式，提供优雅的阅读体验。
*   **主题自适应**：无论是使用 Darcula 暗色主题还是 IntelliJ Light 亮色主题，插件界面都能无缝融入。
*   **历史记录**：自动保存当前会话的完整聊天记录。
*   **上下文感知**：能够感知您当前在 PyCharm 中打开的项目，并将其作为 `gemini` 命令的工作目录。
*   **代理与认证支持**：支持通过配置环境变量来使用 HTTP/HTTPS 代理和设置 `GEMINI_API_KEY`。

## 🚀 安装与使用

目前，该插件需要从源代码进行构建。

### 先决条件

1.  **Java 11 或更高版本**：用于运行 Gradle 构建。
2.  **`gemini` 命令行工具**：确保您已经安装了 [Google Gemini CLI](https://github.com/google/gemini-cli) 并且可以在终端中直接运行 `gemini` 命令。
3.  **PyCharm IDE**: 用于开发和运行插件。

### 构建步骤

1.  **克隆仓库**
    ```bash
    git clone https://github.com/your-username/your-repo.git
    cd your-repo
    ```

2.  **配置**
    打开 `src/main/kotlin/com/example/geminichat/GeminiToolWindow.kt` 文件，修改以下两个变量：
    ```kotlin
    // 您的 Gemini API 密钥
    private val geminiApiKey = "YOUR_GEMINI_API_TOKEN" 
    // 您的本地 HTTP/HTTPS 代理地址 (如果需要)
    private val proxyUrl = "http://127.0.0.1:7897"
    ```

3.  **运行插件**
    在项目根目录打开终端，运行以下命令：
    ```bash
    # For Windows
    .\gradlew.bat runIde

    # For macOS / Linux
    ./gradlew runIde
    ```
    这将会启动一个独立的 PyCharm "沙箱" 实例，并在其中加载好本插件。

4.  **打开工具窗口**
    在启动的沙箱 PyCharm 实例中，点击右侧边栏的 **"Gemini Chat"** 图标，即可开始使用。

## 🛠️ 开发

欢迎对本项目进行贡献！

*   **构建项目**: `.\gradlew.bat build`
*   **运行测试**: `.\gradlew.bat test`
*   **代码风格**: 本项目遵循标准的 Kotlin 编码规范。

### 项目结构

*   `build.gradle.kts`: 项目的 Gradle 构建脚本。
*   `src/main/resources/META-INF/plugin.xml`: 插件的描述文件，定义了插件的名称、扩展点等。
*   `src/main/kotlin/com/example/geminichat/`: 插件的主要源代码。
    *   `GeminiToolWindowFactory.kt`: 创建和初始化工具窗口的工厂类。
    *   `GeminiToolWindow.kt`: 插件的核心，包含了 UI 界面和与 `gemini` 命令交互的所有逻辑。

## 📄 许可证

本项目采用 [MIT License](LICENSE)。
