package dev.gaphunter.rubyshellinjectioncompanion.model

data class ShellInjectionHit(val callText: String, val lineNumber: Int, val columnStart: Int, val columnEnd: Int)
