plugins {
    id("org.jetbrains.intellij") version "1.17.1"
    kotlin("jvm") version "1.9.20"
}

group = "com.dideng.MPTT"
version = "1.0"

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
}

intellij {
    version.set("2023.1.3")
    type.set("PC")
    plugins.set(listOf("python-ce"))
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("251.*")
        changeNotes.set(
            """
            <ul>
                <li>Initial release: Smart completion for Tkinter protocol strings like 'WM_DELETE_WINDOW'.</li>
            </ul>
            """.trimIndent()
        )
    }

    runIde {
        // 可选：调试时传参
        // jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}