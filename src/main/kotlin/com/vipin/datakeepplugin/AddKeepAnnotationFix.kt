package com.vipin.datakeepplugin

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory

class AddKeepAnnotationFix : LocalQuickFix {
    override fun getFamilyName() = "Add @Keep annotation"
    override fun getName() = "Add @Keep"

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val klass = descriptor.psiElement.parent as? KtClass ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            val factory = KtPsiFactory(project)
            val annotation = factory.createAnnotationEntry("@androidx.annotation.Keep")
            klass.addAnnotationEntry(annotation)
        }
    }
}