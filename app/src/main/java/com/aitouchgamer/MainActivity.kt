package com.aitouchgamer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import com.aitouchgamer.data.LocalProfileStore
import com.aitouchgamer.ui.AITouchGamerApp
import com.aitouchgamer.ui.AppViewModel
class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  val store = LocalProfileStore(applicationContext)
  setContent { MaterialTheme { Surface { AITouchGamerApp(remember { AppViewModel(store) }) } } }
 }
}
