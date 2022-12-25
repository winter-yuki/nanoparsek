plugins {
    java
    kotlin("jvm") version "1.7.20" apply false
}

group = "com.github.winteryuki.nanoparsek"
version = "1.0-SNAPSHOT"

subprojects {
    apply {
        plugin("java")
        plugin("kotlin")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation(kotlin("test"))
        testImplementation(platform("org.junit:junit-bom:5.8.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    sourceSets {
        main {
            java.setSrcDirs(listOf("src"))
            resources.setSrcDirs(listOf("resources"))
        }
        test {
            java.setSrcDirs(listOf("test"))
            resources.setSrcDirs(listOf("testResources"))
        }
    }

    tasks.test {
        useJUnitPlatform()
    }

    java.toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
    val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks

    compileKotlin.kotlinOptions {
        languageVersion = "1.8"
        freeCompilerArgs += "-Xuse-k2"
        freeCompilerArgs += "-Xcontext-receivers"
    }

    compileTestKotlin.kotlinOptions {
        languageVersion = "1.8"
        freeCompilerArgs += "-Xuse-k2"
        freeCompilerArgs += "-Xcontext-receivers"
    }
}
