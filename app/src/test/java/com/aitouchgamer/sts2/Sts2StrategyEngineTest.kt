package com.aitouchgamer.sts2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Sts2StrategyEngineTest {
    private val engine = Sts2StrategyEngine()

    @Test fun lethalAttackIsPrioritised() {
        val state = Sts2CombatState(Sts2Character.IRONCLAD, 50, 80, 0, 2,
            listOf(Sts2Enemy("cultist", 6, incomingDamage = 8, intent = EnemyIntent.ATTACK)),
            listOf(Sts2CardInHand("strike", "Strike", 1, Sts2CardRole.ATTACK, damage = 6),
                Sts2CardInHand("defend", "Defend", 1, Sts2CardRole.BLOCK, block = 5)))
        val plan = engine.plan(state)
        assertEquals("strike", plan.orderedPlays.first().cardId)
        assertEquals(0, plan.incomingDamage)
    }

    @Test fun blockPreventsProjectedHpLoss() {
        val state = Sts2CombatState(Sts2Character.SILENT, 20, 70, 0, 1,
            listOf(Sts2Enemy("jaw", 40, incomingDamage = 12)),
            listOf(Sts2CardInHand("defend", "Defend", 1, Sts2CardRole.BLOCK, block = 12)))
        assertEquals(0, engine.plan(state).estimatedHpLoss)
    }

    @Test fun cardsAboveEnergyAreNotPlayed() {
        val state = Sts2CombatState(Sts2Character.REGENT, 75, 75, 0, 1,
            listOf(Sts2Enemy("enemy", 50)),
            listOf(Sts2CardInHand("expensive", "Expensive", 2, Sts2CardRole.ATTACK, damage = 50)))
        assertTrue(engine.plan(state).orderedPlays.isEmpty())
    }
}
