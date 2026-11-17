package com.aitouchgamer.sts2

/**
 * Confidence-tagged OCR evidence. The adapter intentionally does not infer card rules from names.
 * Values are accepted only when an explicit label/value pair is recognised.
 */
data class Sts2OcrField<T>(val value: T, val confidence: Float, val rawText: String) {
    fun trusted(minimumConfidence: Float = 0.82f): T? =
        value.takeIf { confidence.isFinite() && confidence >= minimumConfidence }
}

data class Sts2ScreenEvidence(
    val playerHp: Sts2OcrField<Int>?,
    val maxHp: Sts2OcrField<Int>?,
    val energy: Sts2OcrField<Int>?,
    val block: Sts2OcrField<Int>?,
    val rawLines: List<String>,
    val warnings: List<String>
) {
    val hasTrustedCoreState: Boolean
        get() = playerHp?.trusted() != null && maxHp?.trusted() != null && energy?.trusted() != null

    fun trustedCoreValues(): Map<String, Int> = buildMap {
        playerHp?.trusted()?.let { put("playerHp", it) }
        maxHp?.trusted()?.let { put("maxHp", it) }
        energy?.trusted()?.let { put("energy", it) }
        block?.trusted()?.let { put("block", it) }
    }
}

class Sts2OcrEvidenceAdapter {
    private val patterns = listOf(
        "playerHp" to Regex("""(?i)(?:hp|health)\s*[:：]?\s*(\d{1,3})\s*/\s*(\d{1,3})"""),
        "energy" to Regex("""(?i)(?:energy|エナジー)\s*[:：]?\s*(\d{1,2})"""),
        "block" to Regex("""(?i)(?:block|guard)\s*[:：]?\s*(\d{1,3})""")
    )

    fun read(lines: List<Pair<String, Float>>): Sts2ScreenEvidence {
        var hp: Sts2OcrField<Int>? = null
        var maxHp: Sts2OcrField<Int>? = null
        var energy: Sts2OcrField<Int>? = null
        var block: Sts2OcrField<Int>? = null
        val warnings = mutableListOf<String>()
        for ((raw, confidence) in lines) {
            val text = raw.trim()
            if (text.isEmpty()) continue
            val hpMatch = patterns[0].second.find(text)
            if (hpMatch != null) {
                val current = hpMatch.groupValues[1].toIntOrNull()
                val maximum = hpMatch.groupValues[2].toIntOrNull()
                if (current != null && maximum != null && maximum > 0 && current <= maximum) {
                    hp = Sts2OcrField(current, confidence, text)
                    maxHp = Sts2OcrField(maximum, confidence, text)
                } else warnings += "Ignored invalid HP text: $text"
                continue
            }
            val energyMatch = patterns[1].second.find(text)
            if (energyMatch != null) {
                energyMatch.groupValues[1].toIntOrNull()?.let { energy = Sts2OcrField(it, confidence, text) }
                continue
            }
            val blockMatch = patterns[2].second.find(text)
            if (blockMatch != null) {
                blockMatch.groupValues[1].toIntOrNull()?.let { block = Sts2OcrField(it, confidence, text) }
            }
        }
        if (hp == null) warnings += "Player HP not confidently recognised."
        if (energy == null) warnings += "Energy not confidently recognised."
        return Sts2ScreenEvidence(hp, maxHp, energy, block, lines.map { it.first }, warnings)
    }
}
