package com.aitouchgamer.sts2

enum class Sts2Character(val displayName: String, val mechanic: String, val mainBranch: Boolean) {
    IRONCLAD("Ironclad", "Strength, Exhaust, self-damage and sustain", true),
    SILENT("Silent", "Poison, Shivs, draw and discard", true),
    REGENT("Regent", "Stars, Forge and Sovereign Blade", true),
    NECROBINDER("Necrobinder", "Osty, Summon, Souls and Doom", true),
    DEFECT("Defect", "Lightning, Frost, Dark, Plasma and Focus", true),
    THE_FOOL("The Fool", "Aim, arrows, Impale and melodies", false)
}
enum class Sts2CardRole { ATTACK, BLOCK, DRAW, SCALING, DEBUFF, ENERGY, AOE, FLEX }
enum class EnemyIntent { ATTACK, MULTI_ATTACK, DEBUFF, BUFF, UNKNOWN }
data class Sts2Enemy(val id: String, val hp: Int, val block: Int = 0, val incomingDamage: Int = 0,
    val intent: EnemyIntent = EnemyIntent.UNKNOWN, val vulnerable: Int = 0, val weak: Int = 0, val alive: Boolean = hp > 0)
data class Sts2CardInHand(val id: String, val name: String, val cost: Int, val role: Sts2CardRole,
    val damage: Int = 0, val block: Int = 0, val draw: Int = 0, val strength: Int = 0,
    val appliesVulnerable: Int = 0, val appliesWeak: Int = 0, val hits: Int = 1,
    val targetsAllEnemies: Boolean = false, val exhaust: Boolean = false, val isPlayable: Boolean = true)
data class Sts2CombatState(val character: Sts2Character, val playerHp: Int, val maxHp: Int,
    val playerBlock: Int, val energy: Int, val enemies: List<Sts2Enemy>, val hand: List<Sts2CardInHand>,
    val floor: Int = 0, val act: Int = 1, val ascension: Int = 0, val isPlayerTurn: Boolean = true,
    val isBossFight: Boolean = false)
data class Sts2CardChoice(val cardId: String, val cardName: String, val targetEnemyId: String?,
    val score: Double, val explanation: String)
data class Sts2TurnPlan(val orderedPlays: List<Sts2CardChoice>, val projectedDamage: Int,
    val projectedBlock: Int, val incomingDamage: Int, val estimatedHpLoss: Int,
    val endTurnScore: Double, val notes: List<String>)
