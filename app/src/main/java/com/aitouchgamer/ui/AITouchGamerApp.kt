package com.aitouchgamer.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aitouchgamer.game.*
@Composable fun AITouchGamerApp(vm:AppViewModel){
 var tab by remember{mutableStateOf(0)}
 Scaffold(topBar={Column(Modifier.fillMaxWidth().padding(16.dp)){Text("AITouchGamer",style=MaterialTheme.typography.headlineMedium);Text("ملف اللعبة النشط: STS2 · Slay the Spire 2")}},
 bottomBar={NavigationBar{
  NavigationBarItem(selected=tab==0,onClick={tab=0},icon={Text("AI")},label={Text("مستشار اللعب")})
  NavigationBarItem(selected=tab==1,onClick={tab=1},icon={Text("XY")},label={Text("معايرة اللوحة")})
 }}){padding->Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){if(tab==0)CoachScreen(vm)else BoardScreen(vm)}}
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun CoachScreen(vm:AppViewModel){
 Text("ملف الشخصية",style=MaterialTheme.typography.titleLarge)
 var ec by remember{mutableStateOf(false)}
 ExposedDropdownMenuBox(expanded=ec,onExpandedChange={ec=!ec}){
  OutlinedTextField(vm.character.displayName,{},readOnly=true,label={Text("شخصية STS2")},modifier=Modifier.menuAnchor().fillMaxWidth())
  ExposedDropdownMenu(expanded=ec,onDismissRequest={ec=false}){Sts2Character.entries.forEach{c->DropdownMenuItem(text={Text(c.displayName)},onClick={ {vm.setCharacter(c);ec=false} })}}
 }
 Text(vm.character.playstyle,style=MaterialTheme.typography.bodySmall)
 var es by remember{mutableStateOf(false)}
 ExposedDropdownMenuBox(expanded=es,onExpandedChange={es=!es}){
  OutlinedTextField(vm.style.label,{},readOnly=true,label={Text("أسلوب اتخاذ القرار")},modifier=Modifier.menuAnchor().fillMaxWidth())
  ExposedDropdownMenu(expanded=es,onDismissRequest={es=false}){PlayStyle.entries.forEach{s->DropdownMenuItem(text={Text(s.label)},onClick={ {vm.setStyle(s);es=false} })}}
 }
 HorizontalDivider();Text("حالة القتال",style=MaterialTheme.typography.titleLarge)
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Num("HP",vm.hp,{vm.hp=it},Modifier.weight(1f));Num("Max HP",vm.maxHp,{vm.maxHp=it},Modifier.weight(1f))}
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Num("Energy",vm.energy,{vm.energy=it},Modifier.weight(1f));Num("Block",vm.block,{vm.block=it},Modifier.weight(1f))}
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Num("Incoming damage",vm.incoming,{vm.incoming=it},Modifier.weight(1f));Num("Enemy HP",vm.enemyHp,{vm.enemyHp=it},Modifier.weight(1f))}
 Text("البطاقات في يدك",style=MaterialTheme.typography.titleMedium)
 Text("كل سطر: الاسم | الكلفة | الضرر | الدفاع | السحب (اختياري).",style=MaterialTheme.typography.bodySmall)
 OutlinedTextField(vm.cardLines,{vm.cardLines=it},modifier=Modifier.fillMaxWidth().heightIn(min=110.dp),minLines=3,label={Text("مثال: Strike|1|6|0")})
 Button(onClick=vm::analyze,modifier=Modifier.fillMaxWidth()){Text("حلّل أفضل خطوة")}
 vm.advice?.let{a->Card(Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
  Text(a.headline,style=MaterialTheme.typography.titleLarge);a.steps.forEachIndexed{i,step->Text((i+1).toString()+". "+step)}
  Text(a.explanation,style=MaterialTheme.typography.bodySmall);Text("الثقة التقديرية: "+a.confidence+"%",style=MaterialTheme.typography.labelMedium)
 }}}
 Text("الإدخال يدوي في هذه النسخة. لا تنفّذ لمساً آلياً بناءً على التوصية؛ يلزم ربط الرؤية ومعايرة اللوحة أولاً.",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.secondary)
}
@Composable private fun Num(label:String,value:String,onValue:(String)->Unit,modifier:Modifier=Modifier){OutlinedTextField(value,onValue,modifier=modifier,label={Text(label)},singleLine=true)}
@Composable private fun BoardScreen(vm:AppViewModel){
 Text("معايرة Board الهاتف",style=MaterialTheme.typography.titleLarge)
 Text("اختر الـBoard داخل اللعبة أولاً، ثم أدخل أبعاد مساحة اللعب ونقاط الواجهة كنسب مئوية. لا توجد إحداثيات مفترضة أو نقرات تلقائية قبل المعايرة.")
 OutlinedTextField(vm.boardName,{vm.boardName=it},modifier=Modifier.fillMaxWidth(),label={Text("اسم الـBoard / تخطيط اللعبة")})
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(vm.boardWidth,{vm.boardWidth=it},modifier=Modifier.weight(1f),label={Text("العرض px")});OutlinedTextField(vm.boardHeight,{vm.boardHeight=it},modifier=Modifier.weight(1f),label={Text("الارتفاع px")})}
 vm.anchorKeys.forEach{k->val p=vm.anchors[k]?:("" to "")
  Text(when(k){"hand"->"منطقة اليد";"energy"->"مؤشر الطاقة";"enemy"->"منطقة الأعداء";else->"زر إنهاء الدور"},style=MaterialTheme.typography.titleSmall)
  Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(p.first,{vm.updateAnchor(k,x=it)},modifier=Modifier.weight(1f),label={Text("X %")},singleLine=true);OutlinedTextField(p.second,{vm.updateAnchor(k,y=it)},modifier=Modifier.weight(1f),label={Text("Y %")},singleLine=true)}
 }
 Button(onClick=vm::saveBoard,modifier=Modifier.fillMaxWidth()){Text("حفظ معايرة اللوحة محلياً")}
 Text("حالة المعايرة: "+if(vm.boardName.isBlank()||vm.boardWidth.isBlank()||vm.boardHeight.isBlank())"غير مكتملة" else "تم إدخال بيانات اللوحة",color=MaterialTheme.colorScheme.secondary)
}
