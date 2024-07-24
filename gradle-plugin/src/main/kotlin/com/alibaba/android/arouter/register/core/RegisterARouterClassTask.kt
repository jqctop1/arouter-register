package com.alibaba.android.arouter.register.core

import com.alibaba.android.arouter.register.utils.ScanSetting
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

abstract class RegisterARouterClassTask : DefaultTask() {

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    private val interfaceList = listOf(
        "com/alibaba/android/arouter/facade/template/IRouteRoot",
        "com/alibaba/android/arouter/facade/template/IInterceptorGroup",
        "com/alibaba/android/arouter/facade/template/IProviderGroup",
    )

    private val scanSettingMap = mutableMapOf<String, ScanSetting>().also { map ->
        interfaceList.forEach {
            map[it] = ScanSetting(it)
        }
    }

    private val ROUTER_CLASS_PACKAGE_NAME = ScanSetting.ROUTER_CLASS_PACKAGE_NAME.replace('.', '/')
    private val GENERATE_TO_CLASS_FILE_NAME = ScanSetting.GENERATE_TO_CLASS_NAME.replace('.', '/') + ".class"

    @TaskAction
    fun doAction() {
        val jarOutput = JarOutputStream(BufferedOutputStream(FileOutputStream(output.get().asFile)))

        var logisticsJarFile: File? = null

        allJars.get().forEach { file ->
            val jarFile = JarFile(file.asFile)
            jarFile.entries().iterator().forEach { jarEntry ->
                if (jarEntry.name == GENERATE_TO_CLASS_FILE_NAME) {
                    logisticsJarFile = file.asFile
                } else {
                    if (jarEntry.name.startsWith(ROUTER_CLASS_PACKAGE_NAME)
                        && jarEntry.name.endsWith(".class")) {
                        val classVisitor = CollectRouteClassVisitor(Opcodes.ASM9, scanSettingMap)
                        val classReader = ClassReader(jarFile.getInputStream(jarEntry))
                        classReader.accept(classVisitor, ClassReader.SKIP_CODE)
                    }
                    runCatching {
                        jarOutput.putNextEntry(JarEntry(jarEntry.name))
                        jarFile.getInputStream(JarEntry(jarEntry.name)).use { inputStream ->
                            inputStream.copyTo(jarOutput)
                        }
                        jarOutput.closeEntry()
                    }
                }
            }
            jarFile.close()
        }

        allDirectories.get().forEach { dir ->
            dir.asFile.walk().forEach { file ->
                if (file.isFile) {
                    val relativePath = dir.asFile.toURI().relativize(file.toURI()).path
                    if (relativePath.startsWith(ROUTER_CLASS_PACKAGE_NAME)
                        && relativePath.endsWith(".class")) {
                        ClassReader(file.inputStream()).also {
                            it.accept(CollectRouteClassVisitor(Opcodes.ASM9, scanSettingMap), ClassReader.SKIP_CODE)
                        }
                    }
                    jarOutput.putNextEntry(JarEntry(relativePath.replace(File.separatorChar, '/')))
                    file.inputStream().use { inputStream ->
                        inputStream.copyTo(jarOutput)
                    }
                    jarOutput.closeEntry()
                }
            }
        }

        logisticsJarFile?.let {
            val jarFile = JarFile(it)
            jarFile.getInputStream(JarEntry(GENERATE_TO_CLASS_FILE_NAME)).use { input ->
                val classReader = ClassReader(input)
                val classWriter = ClassWriter(classReader, 0)
                classReader.accept(LogisticsCenterClassVisitor(Opcodes.ASM9, classWriter, scanSettingMap), ClassReader.EXPAND_FRAMES)
                jarOutput.putNextEntry(JarEntry(GENERATE_TO_CLASS_FILE_NAME))
                jarOutput.write(classWriter.toByteArray())
            }
            jarOutput.closeEntry()
            jarFile.close()
        }

        jarOutput.close()
    }

}