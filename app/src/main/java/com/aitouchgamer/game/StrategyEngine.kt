package com.aitouchgamer.game
/** Offline deterministic baseline. It only reasons over explicitly supplied values. */
object StrategyEngine {
 fun advise(s:CombatSnapshot):Advice {
  if(s.hp<=0||s.maxHp<=0||s.energy<0||s.currentBlock<0||s.incomingDamage<0||s.enemyHp<0)
   return Advice("بيانات الحالة غير صالحة",listOf("صحّح قيم القتال ثم أعد التحليل."),"لن يصدر المحرك توصية اعتماداً على قيم غير منطقية.",0)
  val affordable=s.cards.filter{it.cost>=0&&it.cost<=s.energy}
  var lethalDamage=0; var lethalCost=0
  affordable.filter{it.damage>0}.sortedByDescending{it.damage}.forEach{
   if(lethalCost+it.cost<=s.energy){lethalDamage+=it.damage;lethalCost+=it.cost}
  }
  if(lethalDamage>=s.enemyHp&&s.enemyHp>0&&s.style!=PlayStyle.CAUTIOUS)
   return Advice("فرصة إنهاء العدو محتملة",listOf("تحقق من درع العدو والتأثيرات قبل التنفيذ.","لا تعتمد خطاً قاتلاً إلا إذا كانت قراءة HP والدرع مؤكدة."),
    "ضرر البطاقات الخام المتاحة قد يبلغ "+lethalDamage+" مقابل "+s.enemyHp+" HP. لم تُحتسب مقاومات أو تأثيرات غير مدخلة.",55)
  val needed=(s.incomingDamage-s.currentBlock).coerceAtLeast(0)
  val chosen=mutableListOf<CardAction>(); var totalBlock=0; var spent=0
  for(card in affordable.filter{it.block>0}.sortedByDescending{it.block}){
   if(spent+card.cost<=s.energy&&totalBlock<needed){chosen+=card;totalBlock+=card.block;spent+=card.cost}
  }
  val steps=mutableListOf<String>()
  if(needed==0) steps+="الدرع الحالي يغطي الضرر المتوقع وفق القيم المدخلة."
  else if(totalBlock>=needed) steps+="استخدم "+chosen.joinToString("، "){it.name}+" لتغطية الضرر المتوقع."
  else { steps+="الدفاع المتاح لا يغطي الضرر بالكامل؛ النقص "+(needed-totalBlock)+"."; if(chosen.isNotEmpty()) steps+="استخدم "+chosen.joinToString("، "){it.name}+" لتقليل الضرر." }
  val remaining=s.energy-spent
  val attack=affordable.filter{it.damage>0&&it !in chosen}.sortedByDescending{it.damage.toDouble()/it.cost.coerceAtLeast(1)}.firstOrNull{it.cost<=remaining}
  if(attack!=null&&(needed==0||totalBlock>=needed||s.style==PlayStyle.AGGRESSIVE))
   steps+="بعد الدفاع، قيّم لعب "+attack.name+" (ضرر خام "+attack.damage+")."
  if(s.style==PlayStyle.CAUTIOUS&&needed>totalBlock) steps+="أسلوب المحافظ: فضّل تقليل الضرر ولا تنفّذ هجوماً اختيارياً."
  return Advice(if(needed>totalBlock)"الأولوية لتقليل الضرر" else "وازن بين الدفاع والهجوم",steps,
   "التقدير مبني على القيم والبطاقات المدخلة يدوياً. لا يحتسب هذا الإصدار تأثيرات أو تفاعلات غير مدخلة، ولا يقرأ الشاشة تلقائياً.",if(s.cards.isEmpty())25 else 60)
 }
}
