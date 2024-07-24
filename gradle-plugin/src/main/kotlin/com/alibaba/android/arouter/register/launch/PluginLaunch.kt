package com.alibaba.android.arouter.register.launch

import com.alibaba.android.arouter.register.core.RegisterARouterClassTask
import com.alibaba.android.arouter.register.utils.Log
import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized

class PluginLaunch : Plugin<Project> {

    override fun apply(project: Project) {
        val isAppProject = project.plugins.hasPlugin(AppPlugin::class.java)
        if (isAppProject) {
            Log.attach(project)
            val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
            androidComponents.onVariants { variant ->
                if (variant.buildType == "release") {
                    val registerRouteClassTask = project.tasks.register(
                        "register${variant.name.capitalized()}ARouterClass",
                        RegisterARouterClassTask::class.java
                    )
                    variant.artifacts
                        .forScope(ScopedArtifacts.Scope.ALL)
                        .use(registerRouteClassTask)
                        .toTransform(
                            ScopedArtifact.CLASSES,
                            RegisterARouterClassTask::allJars,
                            RegisterARouterClassTask::allDirectories,
                            RegisterARouterClassTask::output
                        )
                }
            }
        }
    }

}