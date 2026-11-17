package com.aitouchgamer.ui
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aitouchgamer.data.LocalProfileStore
import com.aitouchgamer.game.*
class AppViewModel(private val store:LocalProfileStore){
 var character by mutableStateOf(store.character());private set
 var style by mutableStateOf(store.style());private set
 var advice by mutableStateOf<Advice?>(null);private set
 var hp by mutableStateOf("60");var maxHp by mutableStateOf("80");var energy by mutableStateOf("3")
 var block by mutableStateOf("0");var incoming by mutableStateOf("0");var enemyHp by mutableStateOf("30")
 var cardLines by mutableStateOf("Strike|1|6|0\nDefend|1|0|5")
 var boardName by mutableStateOf(store.boardName());var boardWidth by mutableStateOf(store.boardWidth());var boardHeight by mutableStateOf(store.boardHeight())
 val anchorKeys=listOf("hand","energy","enemy","end_turn")
 var anchors by mutableStateOf(anchorKeys.associateWith{store.anchor(it)})
 fun setCharacter(v:Sts2Character){character=v;store.saveProfile(character,style);advice=null}
 fun setStyle(v:PlayStyle){style=v;store.saveProfile(character,style);advice=null}
 fun analyze(){
  val cards=cardLines.lines().mapNotNull{line->val p=line.split("|").map{it.trim()};if(p.size<4)null else runCatching{CardAction(p[0],p[1].toInt(),p[2].toInt(),p[3].toInt(),p.getOrNull(4)?.toIntOrNull()?:0)}.getOrNull()}
  val n=listOf(hp,maxHp,energy,block,incoming,enemyHp).map{it.toIntOrNull()}
  if(n.any{it==null}){advice=Advice("تحقق من الأرقام",listOf("أدخل أرقاماً صحيحة في جميع خانات القتال."),"تعذّر تحليل بعض القيم.",0);return}
  advice=StrategyEngine.advise(CombatSnapshot(character,style,n[0]!!,n[1]!!,n[2]!!,n[3]!!,n[4]!!,n[5]!!,cards))
 }
 fun saveBoard()=store.saveBoard(boardName,boardWidth,boardHeight,anchors)
 fun updateAnchor(k:String,x:String?=null,y:String?=null){val old=anchors[k]?:("" to "");anchors=anchors+(k to ((x?:old.first) to (y?:old.second)))}
}
