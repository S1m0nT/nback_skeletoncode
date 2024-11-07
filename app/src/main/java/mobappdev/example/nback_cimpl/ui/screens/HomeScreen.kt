package mobappdev.example.nback_cimpl.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

/**
 * This is the Home screen composable
 *
 * Currently this screen shows the saved highscore
 * It also contains a button which can be used to show that the C-integration works
 * Furthermore it contains two buttons that you can use to start a game
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */

@Composable
fun HomeScreen(
    vm: GameViewModel,
    navController : NavController
) {
    val highscore by vm.highscore.collectAsState()  // Highscore is its own StateFlow
    val gameState by vm.gameState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB3E5FC))
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(32.dp),
                text = "High-Score = $highscore",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )
            // Todo: You'll probably want to change this "BOX" part of the composable
            Box(
                modifier = Modifier.weight(1f)
                    .background(Color(0xFF81D4FA), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Game Type: ${gameState.gameType}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                    Text(
                        text = "N-Back Level: ${gameState.nBack}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                    Text(
                        text = "Event Interval: ${gameState.eventInterval} ms",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                    Text(
                        text = "Total Events in Round: ${gameState.totalEvents}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                    Text(
                        text = "Audio Letters: ${gameState.audioNumbers}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black
                    )
                    Button(
                        onClick = {
                            navController.navigate("settings")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(text = "Settings", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Audio
                Button(onClick = {
                    vm.setGameType(GameType.Audio)
                    vm.startGame()
                    navController.navigate("game")
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.sound_on),
                        contentDescription = "Sound",
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f)
                    )
                }

                Button(
                    onClick = {
                        vm.setGameType(GameType.Visual)
                        vm.startGame()
                        navController.navigate("game")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0288D1)
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.visual),
                        contentDescription = "Visual",
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f)
                    )
                }

                Button(
                    onClick = {
                        vm.setGameType(GameType.AudioVisual)
                        vm.startGame()
                        navController.navigate("game")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    modifier = Modifier
                        .height(48.dp)
                        .width(96.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.sound_on),
                            contentDescription = "Audio",
                            modifier = Modifier.size(20.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.visual),
                            contentDescription = "Visual",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

            }

        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    Surface {
        HomeScreen(FakeVM(), navController)
    }
}