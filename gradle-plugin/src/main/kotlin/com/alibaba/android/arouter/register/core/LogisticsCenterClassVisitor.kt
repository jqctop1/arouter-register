package com.alibaba.android.arouter.register.core

import com.alibaba.android.arouter.register.utils.Log
import com.alibaba.android.arouter.register.utils.ScanSetting
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class LogisticsCenterClassVisitor(api: Int, classVisitor: ClassVisitor, private val scanSettings: Map<String, ScanSetting>) : ClassVisitor(api, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name == ScanSetting.GENERATE_TO_METHOD_NAME) {
            methodVisitor = LogisticsMethodVisitor(api, methodVisitor, scanSettings)
        }
        return methodVisitor
    }
}

class LogisticsMethodVisitor(api: Int, methodVisitor: MethodVisitor?, private val scanSettings: Map<String, ScanSetting>) : MethodVisitor(api, methodVisitor) {


    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
            scanSettings.values.forEach { scanSetting ->
                scanSetting.classSet.forEach { className ->
                    Log.w("register $className to LogisticsCenter")
                    mv.visitLdcInsn(className.replace('/', '.'))
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        ScanSetting.GENERATE_TO_CLASS_NAME.replace('.', '/'),
                        ScanSetting.REGISTER_METHOD_NAME,
                        "(Ljava/lang/String;)V",
                        false)
                }
            }
        }
        super.visitInsn(opcode)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        super.visitMaxs(maxStack + 4, maxLocals)
    }
}