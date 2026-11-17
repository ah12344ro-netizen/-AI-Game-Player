package com.aitouchgamer.data
import android.content.Context
import com.aitouchgamer.game.PlayStyle
import com.aitouchgamer.game.Sts2Character
class LocalProfileStore(context:Context){
 private val prefs=context.getSharedPreferences("aitouchgamer_local",Context.MODE_PRIVATE)
 fun character()=runCatching{Sts2Character.valueOf(prefs.getString("character",Sts2Character.IRONCLAD.name)!!)}.getOrDefault(Sts2Character.IRONCLAD)
 fun style()=runCatching{PlayStyle.valueOf(prefs.getString("style",PlayStyle.BALANCED.name)!!)}.getOrDefault(PlayStyle.BALANCED)
 fun saveProfile(c:Sts2Character,s:PlayStyle){prefs.edit().putString("character",c.name).putString("style",s.name).apply()}
 fun saveBoard(name:String,width:String,height:String,anchors:Map<String,Pair<String,String>>){
  val e=prefs.edit().putString("board_name",name).putString("board_width",width).putString("board_height",height)
  anchors.forEach{(k,v)->e.putString("anchor_"+k+"_x",v.first).putString("anchor_"+k+"_y",v.second)}
  e.apply()
 }
 fun boardName()=prefs.getString("board_name","")?:""
 fun boardWidth()=prefs.getString("board_width","")?:""
 fun boardHeight()=prefs.getString("board_height","")?:""
 fun anchor(k:String)=(prefs.getString("anchor_"+k+"_x","")?:"") to (prefs.getString("anchor_"+k+"_y","")?:"")
}
