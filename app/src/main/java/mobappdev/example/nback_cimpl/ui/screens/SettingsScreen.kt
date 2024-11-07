package mobappdev.example.nback_cimpl.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(vm: GameViewModel, navController: NavController) {
    val gameState by vm.gameState.collectAsState()
    val nBackLevel = remember { mutableStateOf(gameState.nBack) }
    val eventInterval = remember { mutableStateOf(gameState.eventInterval) }
    val totalEvents = remember { mutableStateOf(gameState.totalEvents) }
    val gridSizeOptions = listOf(3, 4, 5)
    var gridSizeIndex by remember { mutableStateOf(gridSizeOptions.indexOf(gameState.gridSize)) }
    val audioNumbers = remember { mutableStateOf(gameState.audioNumbers) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFB3E5FC)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Game Settings", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Text("N-Back Level", style = MaterialTheme.typography.headlineSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    IconButton(onClick = {
                        if (nBackLevel.value > 1) nBackLevel.value -= 1
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Decrease N-Back Level")
                    }

                    Text(text = nBackLevel.value.toString(), style = MaterialTheme.typography.bodyLarge)

                    IconButton(onClick = {
                        nBackLevel.value += 1
                    }) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Increase N-Back Level")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Event Interval (ms)", style = MaterialTheme.typography.headlineSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    IconButton(onClick = {
                        if (eventInterval.value > 500) eventInterval.value -= 100
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Decrease Event Interval")
                    }

                    Text(text = eventInterval.value.toString(), style = MaterialTheme.typography.bodyLarge)

                    IconButton(onClick = {
                        eventInterval.value += 100
                    }) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Increase Event Interval")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Total Events in Round", style = MaterialTheme.typography.headlineSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    IconButton(onClick = {
                        if (totalEvents.value > 1) totalEvents.value -= 1
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Decrease Total Events")
                    }

                    Text(text = totalEvents.value.toString(), style = MaterialTheme.typography.bodyLarge)

                    IconButton(onClick = {
                        totalEvents.value += 1
                    }) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Increase Total Events")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Number of Audio Letters", style = MaterialTheme.typography.headlineSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    IconButton(onClick = {
                        if (audioNumbers.value > 3) audioNumbers.value -= 1
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Decrease Audio Letters")
                    }

                    Text(text = audioNumbers.value.toString(), style = MaterialTheme.typography.bodyLarge)

                    IconButton(onClick = {
                        if (audioNumbers.value < 26) audioNumbers.value += 1
                    }) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Increase Audio Letters")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Grid Size", style = MaterialTheme.typography.headlineSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    IconButton(onClick = {
                        if (gridSizeIndex > 0) gridSizeIndex -= 1
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Decrease Grid Size")
                    }

                    Text(
                        text = "${gridSizeOptions[gridSizeIndex]}x${gridSizeOptions[gridSizeIndex]}",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    IconButton(onClick = {
                        if (gridSizeIndex < gridSizeOptions.size - 1) gridSizeIndex += 1
                    }) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Increase Grid Size")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        vm.updateSettings(
                            nBack = nBackLevel.value,
                            eventInterval = eventInterval.value,
                            totalEvents = totalEvents.value,
                            gridSize = gridSizeOptions[gridSizeIndex],
                            audioNumbers = audioNumbers.value
                        )
                        navController.popBackStack()
                    }
                ) {
                    Text("Save Settings")
                }
            }
        }
    )
}
