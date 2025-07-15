 import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.2"
}

// ... existing code ...
group = "com.example.geminichat"
version = "1.0-SNAPSHOT"

repositories {
    // 使用阿里云的 Maven 镜像替换默认的 mavenCentral()
    maven { setUrl("https://maven.aliyun.com/repository/public") }
    mavenCentral() // 保留 mavenCentral 作为备用
}

dependencies {
    // 这是新添加的一行，用于引入 commonmark Markdown 解析库
    implementation("org.commonmark:commonmark:0.22.0")
}

// Configure the IntelliJ plugin
intellij {
    version.set("2023.3.3") // Target PyCharm version
    type.set("PC") // PyCharm Community Edition
    plugins.set(listOf("PythonCore")) // Dependency on the Python plugin
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("233") // Corresponds to 2023.3
        untilBuild.set("241.*") // Corresponds to 2024.1.*
    }
}
