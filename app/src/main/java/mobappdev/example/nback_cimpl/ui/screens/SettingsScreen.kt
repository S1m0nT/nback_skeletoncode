package mobappdev.example.nback_cimpl.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    val eventInterval = remember { mutableStateOf(gameState.eventInterval.toString()) }
    val totalEvents = remember { mutableStateOf(gameState.totalEvents.toString()) }
    val nBackLevel = remember { mutableStateOf(gameState.nBack.toString()) }
    val gridSize = remember { mutableStateOf(3) }

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

                OutlinedTextField(
                    value = nBackLevel.value,
                    onValueChange = { nBackLevel.value = it },
                    label = { Text("N-Back Level") },
                    isError = nBackLevel.value.toIntOrNull() == null
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = eventInterval.value,
                    onValueChange = { eventInterval.value = it },
                    label = { Text("Event Interval (ms)") },
                    isError = eventInterval.value.toLongOrNull() == null
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = totalEvents.value,
                    onValueChange = { totalEvents.value = it },
                    label = { Text("Total Events in Round") },
                    isError = totalEvents.value.toIntOrNull() == null
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Grid Size Selection
                Text("Select Grid Size", style = MaterialTheme.typography.headlineSmall)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 3x3 Option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { gridSize.value = 3 }
                    ) {
                        RadioButton(
                            selected = gridSize.value == 3,
                            onClick = { gridSize.value = 3 }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "3x3")
                    }

                    // 5x5 Option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { gridSize.value = 5 }
                    ) {
                        RadioButton(
                            selected = gridSize.value == 5,
                            onClick = { gridSize.value = 5 }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "5x5")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (nBackLevel.value.toIntOrNull() != null &&
                            eventInterval.value.toLongOrNull() != null &&
                            totalEvents.value.toIntOrNull() != null
                        ) {
                            vm.updateSettings(
                                nBack = nBackLevel.value.toInt(),
                                eventInterval = eventInterval.value.toLong(),
                                totalEvents = totalEvents.value.toInt(),
                                gridSize = gridSize.value
                            )
                            navController.popBackStack()
                        } else {
                            Log.d("SettingsScreen", "Invalid input values")
                        }
                    },
                    enabled = nBackLevel.value.toIntOrNull() != null &&
                            eventInterval.value.toLongOrNull() != null &&
                            totalEvents.value.toIntOrNull() != null
                ) {
                    Text("Save Settings")
                }
            }
        }
    )
}
