package com.alibaba.android.arouter.register.utils

import org.gradle.api.Project
import org.gradle.api.logging.Logger

object Log {

    private val TAG = "ARouter::Register >>> "
    private lateinit var sLogger: Logger

    fun attach(project: Project) {
        sLogger = project.logger
    }

    fun d(info: String) {
        sLogger.debug("$TAG $info")
    }

    fun i(info: String) {
        sLogger.info("$TAG $info")
    }

    fun w(info: String) {
        sLogger.warn("$TAG $info")
    }

    fun e(info: String) {
        sLogger.error("$TAG $info")
    }

}