import org.jetbrains.kotlin.gradle.plugin.KaptExtension

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.alibaba.arouter.register")
}

android {
    namespace = "com.example.app"
    defaultConfig {
        applicationId = "com.example.app"
        compileSdkVersion = "android-34"
        minSdk = 24
        versionName = "0.0.1"
        versionCode = 1
        viewBinding {
            enable = true
        }
    }

    extensions.findByType(KaptExtension::class.java)?.let { kapt ->
        kapt.arguments {
            arg("AROUTER_MODULE_NAME", project.name)
        }
    }

    kotlin.jvmToolchain(jdkVersion = 11)
}

dependencies {
    implementation("androidx.activity:activity:1.8.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    kapt("com.alibaba:arouter-compiler:1.5.2")
    implementation("com.alibaba:arouter-api:1.5.2")
    implementation(project(":mylibrary"))
}