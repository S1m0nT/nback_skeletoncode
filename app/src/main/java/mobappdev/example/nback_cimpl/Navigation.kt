package mobappdev.example.nback_cimpl

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mobappdev.example.nback_cimpl.ui.screens.GameScreen
import mobappdev.example.nback_cimpl.ui.screens.HomeScreen
import mobappdev.example.nback_cimpl.ui.viewmodels.GameVM

@Composable
fun Navigation(vm: GameVM, tts: TextToSpeech) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(vm = vm, navController = navController)
        }
        composable("game") {
            GameScreen(
                vm = vm,
                navController = navController,
                tts = tts
            )
        }
    }
}
