package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.MindfulBreathApp
import com.example.ui.theme.MindfulBreathTheme
import com.example.viewmodel.BreathingViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: BreathingViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val systemDark = isSystemInDarkTheme()
      val uiState by viewModel.uiState.collectAsState()

      // Initialize theme with system setting if first run
      LaunchedEffect(Unit) {
        if (systemDark && !uiState.isDarkMode) {
          viewModel.toggleDarkMode()
        }
      }

      MindfulBreathTheme(darkTheme = uiState.isDarkMode) {
        MindfulBreathApp(viewModel = viewModel)
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    viewModel.pauseAmbientOnAppBackground()
  }
}
