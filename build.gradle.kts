buildscript {
    repositories {
        mavenLocal() //调试使用
        mavenCentral()
        gradlePluginPortal()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath(kotlin("gradle-plugin", version = "1.9.10"))
        classpath("com.alibaba", "arouter-register", "1.5.5-SNAPSHOT")
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}