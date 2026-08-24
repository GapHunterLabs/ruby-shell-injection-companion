package dev.gaphunter.rubyshellinjectioncompanion.detect

import dev.gaphunter.rubyshellinjectioncompanion.model.ShellInjectionHit

/**
 * Plain-text line scanner for a Ruby file -- flags `system(...)`,
 * `` `...` `` (backticks), `%x[...]`/`%x(...)`, or `exec(...)` whose
 * command string contains `#{...}` interpolation, the sign of a
 * dynamically-built shell command string. Ruby's own official
 * documentation (`command_injection_rdoc`) states: "Some Ruby core
 * methods accept string data that includes text to be executed as a
 * system command and should not be called with unknown or unsanitized
 * commands" and specifically names `Kernel#exec`, `Kernel#spawn`,
 * `Kernel#system`, and the backtick method. Confirmed real gap: none
 * of RuboCop's 7 Security cops (`Eval`, `Open`, `IoMethods`,
 * `MarshalLoad`, `YAMLLoad`, `JSONLoad`, `CompoundHash`) cover this.
 *
 * **v0.1 scope, stated honestly:** plain-text regex matching, not real
 * Ruby PSI -- doesn't trace whether the interpolated value actually
 * originates from untrusted input, so a `#{}` interpolation of a
 * hardcoded constant is a possible (rare) false positive. The safe
 * `system(cmd, arg1, arg2)` array form (separate arguments, no shell
 * interpretation) is correctly never flagged, since the command string
 * itself has no interpolation in that call shape.
 */
object ShellInjectionScanner {

    private val SYSTEM_OR_EXEC_CALL = Regex("""\b(system|exec)\s*\(\s*(["'])(?:[^"'\\]|\\.)*#\{""")
    private val BACKTICK_CALL = Regex("""`[^`]*#\{""")
    private val PERCENT_X_CALL = Regex("""%x[\[(][^\])]*#\{""")

    fun scan(text: String): List<ShellInjectionHit> {
        val hits = mutableListOf<ShellInjectionHit>()
        text.lines().forEachIndexed { index, rawLine ->
            val trimmed = rawLine.trimStart()
            if (trimmed.startsWith("#")) return@forEachIndexed

            SYSTEM_OR_EXEC_CALL.find(rawLine)?.let { match ->
                hits += ShellInjectionHit(match.groupValues[1], index + 1, match.range.first, match.range.last + 1)
            }
            if (BACKTICK_CALL.containsMatchIn(rawLine)) {
                val match = BACKTICK_CALL.find(rawLine)!!
                hits += ShellInjectionHit("`` (backtick)", index + 1, match.range.first, match.range.last + 1)
            }
            if (PERCENT_X_CALL.containsMatchIn(rawLine)) {
                val match = PERCENT_X_CALL.find(rawLine)!!
                hits += ShellInjectionHit("%x", index + 1, match.range.first, match.range.last + 1)
            }
        }
        return hits
    }
}
