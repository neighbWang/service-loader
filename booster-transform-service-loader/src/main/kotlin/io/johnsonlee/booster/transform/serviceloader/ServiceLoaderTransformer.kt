package io.johnsonlee.booster.transform.serviceloader

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.Transformer
import com.google.auto.service.AutoService
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Attribute
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.TypePath

/**
 * Represents a transformer which used to replace [java.util.ServiceLoader] with [ShadowServiceLoader]
 *
 * @author johnsonlee
 */
@AutoService(Transformer::class)
class ServiceLoaderTransformer : Transformer {

    override fun transform(context: TransformContext, bytecode: ByteArray) = ClassReader(bytecode).let { reader ->
        ClassWriter(reader, ClassWriter.COMPUTE_FRAMES).let { writer ->
            reader.accept(ServiceLoaderClassVisitor(writer), 0)
            writer.toByteArray()
        }
    }

}

internal const val JAVA_UTIL_SERVICE_LOADER = "java/util/ServiceLoader"

internal const val SHADOW_SERVICE_LOADER = "io/johnsonlee/booster/instrument/ShadowServiceLoader"

internal fun String.replaceServiceLoader() = this.replace(JAVA_UTIL_SERVICE_LOADER, SHADOW_SERVICE_LOADER)

internal class ServiceLoaderClassVisitor(visitor: ClassVisitor) : ClassVisitor(Opcodes.ASM7, visitor) {

    override fun visitMethod(access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor?.replaceServiceLoader(), signature?.replaceServiceLoader(), exceptions)
        return ServiceLoaderMethodVisitor(mv)
    }

    override fun visitInnerClass(name: String?, outerName: String?, innerName: String?, access: Int) {
        super.visitInnerClass(name, outerName, innerName, access)
    }

    override fun visitOuterClass(owner: String?, name: String?, descriptor: String?) {
        super.visitOuterClass(owner, name, descriptor)
    }

    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature?.replaceServiceLoader(), superName, interfaces)
    }

    override fun visitField(access: Int, name: String?, descriptor: String?, signature: String?, value: Any?): FieldVisitor {
        return super.visitField(access, name, descriptor?.replaceServiceLoader(), signature?.replaceServiceLoader(), value)
    }

}

internal class ServiceLoaderMethodVisitor(visitor: MethodVisitor) : MethodVisitor(Opcodes.ASM7, visitor) {

    override fun visitMultiANewArrayInsn(descriptor: String?, numDimensions: Int) {
        super.visitMultiANewArrayInsn(descriptor?.replaceServiceLoader(), numDimensions)
    }

    override fun visitTryCatchBlock(start: Label?, end: Label?, handler: Label?, type: String?) {
        super.visitTryCatchBlock(start, end, handler, type?.replaceServiceLoader())
    }

    override fun visitTypeInsn(opcode: Int, type: String?) {
        super.visitTypeInsn(opcode, type?.replaceServiceLoader())
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitAnnotation(descriptor?.replaceServiceLoader(), visible)
    }

    override fun visitTypeAnnotation(typeRef: Int, typePath: TypePath?, descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitTypeAnnotation(typeRef, typePath, descriptor?.replaceServiceLoader(), visible)
    }

    override fun visitInvokeDynamicInsn(name: String?, descriptor: String?, bootstrapMethodHandle: Handle?, vararg bootstrapMethodArguments: Any?) {
        super.visitInvokeDynamicInsn(name, descriptor?.replaceServiceLoader(), bootstrapMethodHandle, *bootstrapMethodArguments)
    }

    override fun visitTryCatchAnnotation(typeRef: Int, typePath: TypePath?, descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitTryCatchAnnotation(typeRef, typePath, descriptor?.replaceServiceLoader(), visible)
    }

    override fun visitMethodInsn(opcode: Int, owner: String?, name: String?, descriptor: String?, isInterface: Boolean) {
        super.visitMethodInsn(opcode, owner?.replaceServiceLoader(), name, descriptor?.replaceServiceLoader(), isInterface)
    }

    override fun visitInsnAnnotation(typeRef: Int, typePath: TypePath?, descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitInsnAnnotation(typeRef, typePath, descriptor?.replaceServiceLoader(), visible)
    }

    override fun visitParameterAnnotation(parameter: Int, descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitParameterAnnotation(parameter, descriptor?.replaceServiceLoader(), visible)
    }

    override fun visitLocalVariableAnnotation(typeRef: Int, typePath: TypePath?, start: Array<out Label>?, end: Array<out Label>?, index: IntArray?, descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor?.replaceServiceLoader(), visible)
    }

    override fun visitLocalVariable(name: String?, descriptor: String?, signature: String?, start: Label?, end: Label?, index: Int) {
        super.visitLocalVariable(name, descriptor?.replaceServiceLoader(), signature?.replaceServiceLoader(), start, end, index)
    }

    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        super.visitFieldInsn(opcode, owner, name, descriptor?.replaceServiceLoader())
    }

}