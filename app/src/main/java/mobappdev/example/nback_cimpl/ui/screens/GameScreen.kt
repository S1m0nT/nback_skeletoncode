package mobappdev.example.nback_cimpl.ui.screens

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mobappdev.example.nback_cimpl.ui.viewmodels.FakeVM
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import mobappdev.example.nback_cimpl.ui.viewmodels.MatchStatus

@Composable
fun GameScreen(
    vm: GameViewModel,
    navController: NavController,
    tts: TextToSpeech,
) {
    val gameState by vm.gameState.collectAsState()
    val score by vm.score.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    if (gameState.gameType == GameType.Audio || gameState.gameType == GameType.AudioVisual) {
        LaunchedEffect(gameState.currentAudio) {
            if (gameState.currentAudio?.isNotEmpty() == true) {
                Log.d("GameScreen", "Speaking: ${gameState.currentAudio}")
                tts.speak(gameState.currentAudio, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    val matchColor = when (gameState.matchStatus) {
        MatchStatus.Match -> Color.Green
        MatchStatus.NoMatch -> Color.Red
        else -> Color(0xFF0288D1)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB3E5FC))
                .padding(it),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Current Event: ${gameState.eventNumber }/${gameState.totalEvents}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Score: $score",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            VisualGrid(gridSize = gameState.gridSize, eventValue = gameState.eventValue)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                if (gameState.gameType == GameType.Audio || gameState.gameType == GameType.AudioVisual) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(matchColor, shape = MaterialTheme.shapes.medium)
                            .padding(16.dp)
                            .clickable {
                                vm.checkMatchAudio()
                                Log.d("GameScreen", "Sound button clicked")
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "SOUND", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (gameState.gameType == GameType.Visual || gameState.gameType == GameType.AudioVisual) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(matchColor, shape = MaterialTheme.shapes.medium)
                            .padding(16.dp)
                            .clickable {
                                vm.checkMatchVisual()
                                Log.d("GameScreen", "Position button clicked")
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "POSITION", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Return to Home")
            }
        }
    }
}

@Composable
fun VisualGrid(gridSize: Int, eventValue: Int) {
    val gridItemCount = gridSize * gridSize

    LazyVerticalGrid(
        columns = GridCells.Fixed(gridSize),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        items(gridItemCount) { index ->
            val isActive = index == eventValue
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .aspectRatio(1f)
                    .background(
                        color = if (isActive) Color.Green else Color(0xFF81D4FA),
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable { }
            )
        }
    }
}

@Preview
@Composable
fun GameScreenPreview() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tts = remember {
        TextToSpeech(context, null)
    }
    GameScreen(
        FakeVM(), navController,
        tts = tts
    )
}
