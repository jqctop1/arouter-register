
apply(rootProject.file("gradle/jar-publish.gradle"))

plugins {
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
}

dependencies {
    implementation("com.android.tools.build:gradle:8.0.0")
    implementation("commons-codec:commons-codec:1.15")
    implementation("commons-io:commons-io:2.13.0")
    implementation("org.ow2.asm:asm-util:9.6")
}

ext {
    set("POM_GROUP", "com.alibaba")
    set("POM_ARTIFACT_ID", "arouter-register")
    set("POM_VERSION", "1.5.2")
}

gradlePlugin {
    plugins {
        register("arouter-register-agp") {
            id = "com.alibaba.arouter.register"
            implementationClass = "com.alibaba.android.arouter.register.launch.PluginLaunch"
        }
    }
}