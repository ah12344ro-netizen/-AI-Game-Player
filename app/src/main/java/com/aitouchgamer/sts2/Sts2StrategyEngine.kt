package com.aitouchgamer.sts2

import kotlin.math.max
import kotlin.math.min

/** Explainable offline planner. Unknown card/enemy effects are never inferred from names. */
class Sts2StrategyEngine {
    fun plan(state: Sts2CombatState): Sts2TurnPlan {
        if (!state.isPlayerTurn) return Sts2TurnPlan(emptyList(), 0, 0, 0, 0, 0.0, listOf("Not the player's turn."))
        val enemies = state.enemies.filter { it.alive && it.hp > 0 }
        val remaining = state.hand.filter { it.isPlayable && it.cost >= 0 }.toMutableList()
        var energy = state.energy.coerceAtLeast(0)
        var block = state.playerBlock.coerceAtLeast(0)
        var damage = 0
        val plays = mutableListOf<Sts2CardChoice>()
        val hp = enemies.associate { it.id to it.hp }.toMutableMap()
        val enemyBlocks = enemies.associate { it.id to it.block.coerceAtLeast(0) }.toMutableMap()
        val weak = enemies.associate { it.id to it.weak }.toMutableMap()
        val vulnerable = enemies.associate { it.id to it.vulnerable }.toMutableMap()

        while (true) {
            val best = remaining.filter { it.cost <= energy }.mapNotNull { card ->
                val target = chooseTarget(card, enemies, hp)
                if (needsTarget(card) && target == null) null
                else Triple(card, target, score(card, state, enemies, hp, enemyBlocks, block, target, vulnerable))
            }.filter { it.third > 0.0 }.maxByOrNull { it.third } ?: break
            val (card, target, value) = best
            energy -= card.cost
            remaining.remove(card)
            val targets = if (card.targetsAllEnemies) enemies.filter { (hp[it.id] ?: 0) > 0 }
                else listOfNotNull(enemies.firstOrNull { it.id == target && (hp[it.id] ?: 0) > 0 })
            val dealt = if (card.damage > 0 || card.role == Sts2CardRole.ATTACK || card.role == Sts2CardRole.AOE) targets.sumOf { enemy ->
                var total = 0
                repeat(max(1, card.hits)) {
                    val raw = if ((vulnerable[enemy.id] ?: 0) > 0) (card.damage * 1.5).toInt() else card.damage
                    val absorbed = min(enemyBlocks[enemy.id] ?: 0, raw)
                    enemyBlocks[enemy.id] = max(0, (enemyBlocks[enemy.id] ?: 0) - absorbed)
                    val amount = min(hp[enemy.id] ?: 0, max(0, raw - absorbed))
                    hp[enemy.id] = max(0, (hp[enemy.id] ?: 0) - amount)
                    total += amount
                }
                total
            } else 0
            damage += dealt
            block += card.block
            if (target != null && card.appliesWeak > 0) weak[target] = max(weak[target] ?: 0, card.appliesWeak)
            if (target != null && card.appliesVulnerable > 0) vulnerable[target] = max(vulnerable[target] ?: 0, card.appliesVulnerable)
            val reason = when {
                dealt > 0 && hp.values.all { it <= 0 } -> "Lethal: removes the remaining modelled enemy HP."
                dealt > 0 -> "Deals $dealt projected damage after known enemy Block and Vulnerable."
                card.block > 0 -> "Block offsets incoming damage."
                card.draw > 0 -> "Draw effect; new card identities are not simulated."
                card.appliesWeak > 0 -> "Weak reduces enemy attack pressure."
                card.appliesVulnerable > 0 -> "Vulnerable improves later attacks."
                card.strength > 0 -> "Strength scales later attacks."
                else -> "Utility play from supplied card properties."
            }
            plays += Sts2CardChoice(card.id, card.name, target, value, reason)
            if (hp.values.all { it <= 0 }) break
        }
        val incoming = enemies.filter { (hp[it.id] ?: 0) > 0 }.sumOf {
            if ((weak[it.id] ?: 0) > 0) (it.incomingDamage * 0.75).toInt() else it.incomingDamage
        }
        val loss = max(0, incoming - block)
        val notes = mutableListOf<String>()
        if (loss > 0) notes += "Estimated HP loss if the turn ends: $loss."
        if (remaining.any { it.cost > energy }) notes += "Some remaining cards exceed the estimated energy."
        if (state.hand.any { it.draw > 0 }) notes += "Drawn cards are not modelled until a new observation arrives."
        when (state.character) {
            Sts2Character.REGENT -> notes += "Include Stars and Sovereign Blade state when known."
            Sts2Character.NECROBINDER -> notes += "Include Osty, Summon and Doom state when known."
            Sts2Character.DEFECT -> notes += "Include orb slots and Focus when known."
            Sts2Character.THE_FOOL -> notes += "The Fool is experimental content; verify the active game build."
            else -> Unit
        }
        return Sts2TurnPlan(plays, damage, max(0, block - state.playerBlock), incoming, loss,
            if (loss >= state.playerHp) -100.0 else if (loss > 0) -loss * 2.0 else 1.0, notes)
    }

    private fun needsTarget(c: Sts2CardInHand) = c.damage > 0 || c.role == Sts2CardRole.ATTACK ||
        c.role == Sts2CardRole.AOE || c.appliesWeak > 0 || c.appliesVulnerable > 0

    private fun chooseTarget(c: Sts2CardInHand, enemies: List<Sts2Enemy>, hp: Map<String, Int>): String? {
        if (!needsTarget(c)) return null
        return enemies.filter { (hp[it.id] ?: 0) > 0 }.maxByOrNull { e ->
            val lethal = if (c.damage * max(1, c.hits) >= (hp[e.id] ?: e.hp)) 100 else 0
            e.incomingDamage * 2 + lethal - (hp[e.id] ?: e.hp) / 10
        }?.id
    }

    private fun score(c: Sts2CardInHand, s: Sts2CombatState, enemies: List<Sts2Enemy>,
        hp: Map<String, Int>, enemyBlocks: Map<String, Int>, currentBlock: Int,
        target: String?, vulnerable: Map<String, Int>): Double {
        val living = enemies.filter { (hp[it.id] ?: 0) > 0 }
        val targets = if (c.targetsAllEnemies) living else listOfNotNull(living.firstOrNull { it.id == target })
        val damage = targets.sumOf { enemy -> projectedDamage(c, enemy, hp, enemyBlocks, vulnerable) }
        val targetHp = target?.let { hp[it] ?: 0 } ?: 0
        val targetEnemy = enemies.firstOrNull { it.id == target }
        val lethal = targetEnemy != null && projectedDamage(c, targetEnemy, hp, enemyBlocks, vulnerable) >= targetHp
        val incoming = living.sumOf { it.incomingDamage }
        val blockGain = min(c.block, max(0, incoming - currentBlock))
        var result = when (c.role) {
            Sts2CardRole.ATTACK -> damage.toDouble()
            Sts2CardRole.AOE -> damage * 1.15
            Sts2CardRole.BLOCK -> blockGain * 1.35
            Sts2CardRole.DRAW -> c.draw * 2.2
            Sts2CardRole.SCALING -> c.strength * max(1, living.size) * 2.0
            Sts2CardRole.DEBUFF -> (c.appliesWeak * 2.0 + c.appliesVulnerable * 2.2) * max(1, living.size)
            Sts2CardRole.ENERGY -> 3.5
            Sts2CardRole.FLEX -> damage * 0.8 + blockGain
        }
        if (lethal) result += 30.0 + (targetEnemy?.incomingDamage ?: 0)
        if (s.playerHp <= s.maxHp * 0.35f && c.block > 0) result *= 1.25
        if (incoming > currentBlock && c.block > 0) result += 2.0
        if (s.isBossFight && c.strength > 0) result += 2.0
        return result - c.cost * 0.35
    }

    private fun projectedDamage(
        card: Sts2CardInHand,
        enemy: Sts2Enemy,
        hp: Map<String, Int>,
        enemyBlocks: Map<String, Int>,
        vulnerable: Map<String, Int>
    ): Int {
        var remainingHp = hp[enemy.id] ?: 0
        var remainingBlock = enemyBlocks[enemy.id] ?: 0
        var total = 0
        repeat(max(1, card.hits)) {
            val raw = if ((vulnerable[enemy.id] ?: 0) > 0) (card.damage * 1.5).toInt() else card.damage
            val absorbed = min(remainingBlock, raw)
            remainingBlock -= absorbed
            val dealt = min(remainingHp, max(0, raw - absorbed))
            remainingHp -= dealt
            total += dealt
        }
        return total
    }
}
