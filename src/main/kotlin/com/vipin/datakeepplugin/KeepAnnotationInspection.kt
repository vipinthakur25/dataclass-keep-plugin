package com.vipin.datakeepplugin

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtVisitorVoid

class KeepAnnotationInspection : LocalInspectionTool() {
    override fun getDisplayName() = "Missing @Keep on data class"
    override fun getGroupDisplayName() = "Android"
    override fun getShortName() = "DataClassMissingKeep"

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean
    ): PsiElementVisitor {
        return object : KtVisitorVoid() {
            override fun visitClass(klass: KtClass) {
                super.visitClass(klass)

                if (!klass.isData()) return

                val hasKeep = klass.annotationEntries.any {
                    it.shortName?.asString() == "Keep"
                }

                if (!hasKeep) {
                    holder.registerProblem(
                        klass.nameIdentifier ?: return,
                        "Data class is missing @Keep annotation",
                        ProblemHighlightType.WARNING,
                        AddKeepAnnotationFix()
                    )
                }
            }
        }
    }
}