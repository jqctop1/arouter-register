package com.alibaba.android.arouter.register.core

import com.alibaba.android.arouter.register.utils.ScanSetting
import org.objectweb.asm.ClassVisitor

class CollectRouteClassVisitor(api: Int, private val result: Map<String, ScanSetting>) : ClassVisitor(api) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
        if (name.isNullOrEmpty()) return
        interfaces?.forEach {
            result[it]?.classSet?.add(name)
        }
    }
}