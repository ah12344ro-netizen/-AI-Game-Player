package com.aitouchgamer.sts2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Sts2OcrEvidenceAdapterTest {
    private val adapter = Sts2OcrEvidenceAdapter()

    @Test fun parsesOnlyExplicitlyLabelledCoreValues() {
        val evidence = adapter.read(listOf(
            "HP 42/80" to 0.97f,
            "Energy: 3" to 0.94f,
            "Block: 12" to 0.91f,
            "Cultist 28" to 0.99f
        ))
        assertTrue(evidence.hasTrustedCoreState)
        assertEquals(42, evidence.trustedCoreValues()["playerHp"])
        assertEquals(80, evidence.trustedCoreValues()["maxHp"])
        assertEquals(3, evidence.trustedCoreValues()["energy"])
        assertEquals(12, evidence.trustedCoreValues()["block"])
    }

    @Test fun lowConfidenceOcrDoesNotBecomeTrustedGameState() {
        val evidence = adapter.read(listOf("HP 42/80" to 0.51f, "Energy: 3" to 0.49f))
        assertFalse(evidence.hasTrustedCoreState)
        assertTrue(evidence.trustedCoreValues().isEmpty())
    }

    @Test fun invalidHpIsRejected() {
        val evidence = adapter.read(listOf("HP 99/80" to 0.99f, "Energy: 2" to 0.99f))
        assertFalse(evidence.hasTrustedCoreState)
        assertTrue(evidence.warnings.any { it.contains("invalid HP") })
    }
}
