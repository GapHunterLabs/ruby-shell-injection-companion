package dev.gaphunter.rubyshellinjectioncompanion.inspection

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import dev.gaphunter.rubyshellinjectioncompanion.detect.ShellInjectionScanner
import dev.gaphunter.rubyshellinjectioncompanion.model.ShellInjectionHit
import dev.gaphunter.rubyshellinjectioncompanion.review.ReviewPrompt

/**
 * Flags `system`/`exec`/backtick/`%x` calls with an interpolated
 * (`#{...}`) command string -- see [ShellInjectionScanner] for the
 * full reasoning. Runs via [checkFile] (whole-file text scan), same
 * discipline as the catalog's other Ruby plugins -- see
 * `build.gradle.kts` for why no Ruby-language PSI dependency is
 * taken.
 */
class ShellCommandInjectionInspection : LocalInspectionTool() {

    companion object {
        const val MAX_FILE_LENGTH = 500_000
        private val RUBY_FILE_NAME = Regex("""^[^.]+\.rb$""", RegexOption.IGNORE_CASE)
    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        val virtualFile = file.virtualFile ?: return null
        if (!RUBY_FILE_NAME.matches(virtualFile.name)) return null

        val text = file.text
        if (text.length > MAX_FILE_LENGTH) return null

        val hits = ShellInjectionScanner.scan(text)
        if (hits.isEmpty()) return null

        val document = file.viewProvider.document ?: return null
        val problems = mutableListOf<ProblemDescriptor>()

        for (hit in hits) {
            if (hit.lineNumber - 1 !in 0 until document.lineCount) continue
            val lineStartOffset = document.getLineStartOffset(hit.lineNumber - 1)
            val absoluteStart = lineStartOffset + hit.columnStart
            val absoluteEnd = lineStartOffset + hit.columnEnd
            val anchor = leafElementAt(file, absoluteStart) ?: continue
            val anchorStart = anchor.textRange.startOffset
            val relativeRange = TextRange(
                (absoluteStart - anchorStart).coerceAtLeast(0),
                (absoluteEnd - anchorStart).coerceAtMost(anchor.textLength),
            )
            if (relativeRange.startOffset >= relativeRange.endOffset) continue

            problems += manager.createProblemDescriptor(
                anchor,
                relativeRange,
                "${hit.callText} runs an interpolated command string through a shell -- if any part comes from " +
                    "untrusted input, this is a command injection vulnerability (CWE-78). Ruby's own docs name " +
                    "this exact risk for system/exec/backticks",
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                isOnTheFly,
            )

            ReviewPrompt.recordHit(file.project, "${virtualFile.path}:${hit.lineNumber}")
        }

        return if (problems.isEmpty()) null else problems.toTypedArray()
    }

    private fun leafElementAt(file: PsiFile, startOffset: Int): PsiElement? {
        if (startOffset < 0 || startOffset >= file.textLength) return null
        var element = file.findElementAt(startOffset) ?: return file
        while (element.firstChild != null) {
            element = element.firstChild
        }
        return element
    }
}
