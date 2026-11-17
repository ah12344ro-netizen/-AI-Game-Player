package com.aitouchgamer.game
import org.junit.Assert.assertEquals
import org.junit.Test
class StrategyEngineTest {
 @Test fun invalidStateHasZeroConfidence(){
  val a=StrategyEngine.advise(CombatSnapshot(Sts2Character.IRONCLAD,PlayStyle.BALANCED,-1,80,3,0,10,20,emptyList()))
  assertEquals(0,a.confidence)
 }
 @Test fun recommendsDefenseWhenIncomingExceedsBlock(){
  val a=StrategyEngine.advise(CombatSnapshot(Sts2Character.IRONCLAD,PlayStyle.BALANCED,50,80,3,0,12,40,listOf(CardAction("Defend",1,block=5))))
  assertEquals("الأولوية لتقليل الضرر",a.headline)
 }
}
