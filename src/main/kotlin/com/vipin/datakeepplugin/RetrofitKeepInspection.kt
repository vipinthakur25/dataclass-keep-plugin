package com.vipin.datakeepplugin

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtVisitorVoid

class RetrofitKeepInspection : LocalInspectionTool() {

    override fun getDisplayName() = "Retrofit response class missing @Keep"
    override fun getGroupDisplayName() = "Android"
    override fun getShortName() = "RetrofitResponseMissingKeep"

    // Retrofit HTTP annotations we look for
    private val retrofitAnnotations = setOf(
        "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "HTTP"
    )

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean
    ): PsiElementVisitor {
        return object : KtVisitorVoid() {
            override fun visitNamedFunction(function: KtNamedFunction) {
                super.visitNamedFunction(function)

                // Check if this function has a Retrofit annotation
                val hasRetrofitAnnotation = function.annotationEntries.any { annotation ->
                    annotation.shortName?.asString() in retrofitAnnotations
                }

                if (!hasRetrofitAnnotation) return

                // Get the return type text e.g. "Call<UserResponse>"
                val returnTypeRef = function.typeReference ?: return
                val returnTypeText = returnTypeRef.text

                // Extract type inside Call<> or Response<>
                val responseTypeName = extractResponseType(returnTypeText) ?: return

                // Find the class declaration in the same file
                val ktFile = function.containingFile as? KtFile ?: return
                val responseClass = findClassInFile(ktFile, responseTypeName) ?: return

                // Check if @Keep is present
                val hasKeep = responseClass.annotationEntries.any {
                    it.shortName?.asString() == "Keep"
                }

                if (!hasKeep) {
                    holder.registerProblem(
                        function.nameIdentifier ?: return,
                        "Retrofit response class '$responseTypeName' is missing @Keep annotation",
                        ProblemHighlightType.WARNING,
                        AddKeepToResponseClassFix(responseClass)
                    )
                }
            }
        }
    }

    // Extracts "UserResponse" from "Call<UserResponse>" or "Response<UserResponse>"
    private fun extractResponseType(returnType: String): String? {
        val regex = Regex("""(?:Call|Response)<(.+)>""")
        return regex.find(returnType)?.groupValues?.get(1)?.trim()
    }

    // Finds a class by name in the same file
    private fun findClassInFile(file: KtFile, className: String): KtClass? {
        return file.declarations
            .filterIsInstance<KtClass>()
            .find { it.name == className }
    }
}