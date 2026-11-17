package com.aitouchgamer.game
enum class Sts2Character(val displayName: String, val playstyle: String) {
 IRONCLAD("Ironclad","Strength, Exhaust, self-damage and direct attacks"),
 SILENT("Silent","Shivs, Poison, discard synergies and flexible defense"),
 REGENT("Regent","Stars, Forge and the Sovereign Blade"),
 NECROBINDER("Necrobinder","Osty, summons, Doom and Souls"),
 DEFECT("Defect","Orbs, Focus and scaling effects")
}
enum class PlayStyle(val label: String) { CAUTIOUS("محافظ"), BALANCED("متوازن"), AGGRESSIVE("هجومي") }
data class CardAction(val name:String,val cost:Int,val damage:Int=0,val block:Int=0,val draw:Int=0)
data class CombatSnapshot(val character:Sts2Character,val style:PlayStyle,val hp:Int,val maxHp:Int,val energy:Int,val currentBlock:Int,val incomingDamage:Int,val enemyHp:Int,val cards:List<CardAction>)
data class Advice(val headline:String,val steps:List<String>,val explanation:String,val confidence:Int)
