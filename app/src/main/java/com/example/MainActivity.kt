package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.NavTab
import com.example.ui.components.ClientBottomNav
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ModsScreen
import com.example.ui.screens.PvpScreen
import com.example.ui.screens.ServersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()

            // Handle incoming .mcpack / .mcworld intents
            LaunchedEffect(intent) {
                handleIncomingIntent(intent, mainViewModel)
            }

            val accentTheme by mainViewModel.accentTheme.collectAsStateWithLifecycle()
            val hudSettings by mainViewModel.hudSettings.collectAsStateWithLifecycle()

            MyApplicationTheme(
                accentTheme = accentTheme,
                powerSaveMode = hudSettings.powerSaveEnabled
            ) {
                MainAppScreen(viewModel = mainViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?, viewModel: MainViewModel) {
        val uri = intent?.data
        if (uri != null) {
            viewModel.handleImportedFileUri(uri)
            viewModel.selectTab(NavTab.MODS)
        }
    }
}

@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            containerColor = DarkBg,
            bottomBar = {
                ClientBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        fadeIn(animationSpec = androidx.compose.animation.core.tween(220)) togetherWith
                                fadeOut(animationSpec = androidx.compose.animation.core.tween(180))
                    },
                    label = "tab_navigation"
                ) { tab ->
                    when (tab) {
                        NavTab.HOME -> HomeScreen(viewModel = viewModel)
                        NavTab.MODS -> ModsScreen(viewModel = viewModel)
                        NavTab.PVP -> PvpScreen(viewModel = viewModel)
                        NavTab.SERVERS -> ServersScreen(viewModel = viewModel)
                        NavTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
