package com.vipin.datakeepplugin

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory

class AddKeepToResponseClassFix(
    private val responseClass: KtClass
) : LocalQuickFix {

    override fun getFamilyName() = "Add @Keep to response class"
    override fun getName() = "Add @Keep to '${responseClass.name}'"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        WriteCommandAction.runWriteCommandAction(project) {
            val factory = KtPsiFactory(project)
            val annotation = factory.createAnnotationEntry("@androidx.annotation.Keep")
            responseClass.addAnnotationEntry(annotation)
        }
    }
}