package dev.gaphunter.rubyshellinjectioncompanion.detect

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ShellInjectionScannerTest {

    @Test
    fun `flags system call with interpolated command string`() {
        val code = """system("ping #{host}")"""
        val hits = ShellInjectionScanner.scan(code)
        assertEquals(1, hits.size)
        assertEquals("system", hits[0].callText)
    }

    @Test
    fun `flags exec call with interpolated command string`() {
        val code = """exec("convert #{filename} out.png")"""
        val hits = ShellInjectionScanner.scan(code)
        assertEquals(1, hits.size)
        assertEquals("exec", hits[0].callText)
    }

    @Test
    fun `flags backtick call with interpolation`() {
        val code = """output = `ls #{directory}`"""
        val hits = ShellInjectionScanner.scan(code)
        assertEquals(1, hits.size)
    }

    @Test
    fun `flags percent-x call with interpolation`() {
        val code = """output = %x[ls #{directory}]"""
        val hits = ShellInjectionScanner.scan(code)
        assertEquals(1, hits.size)
    }

    @Test
    fun `does not flag the safe array-argument form of system`() {
        val code = """system("ping", host)"""
        assertTrue(ShellInjectionScanner.scan(code).isEmpty())
    }

    @Test
    fun `does not flag a system call with a plain literal string`() {
        val code = """system("uptime")"""
        assertTrue(ShellInjectionScanner.scan(code).isEmpty())
    }

    @Test
    fun `does not flag a commented-out line`() {
        val code = """# system("ping #{host}")"""
        assertTrue(ShellInjectionScanner.scan(code).isEmpty())
    }
}
