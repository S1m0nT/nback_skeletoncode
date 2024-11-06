package mobappdev.example.nback_cimpl.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.GameApplication
import mobappdev.example.nback_cimpl.NBackHelper
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository

/**
 * This is the GameViewModel.
 *
 * It is good practice to first make an interface, which acts as the blueprint
 * for your implementation. With this interface we can create fake versions
 * of the viewmodel, which we can use to test other parts of our app that depend on the VM.
 *
 * Our viewmodel itself has functions to start a game, to specify a gametype,
 * and to check if we are having a match
 *
 * Date: 25-08-2023
 * Version: Version 1.0
 * Author: Yeetivity
 *
 */


interface GameViewModel {
    val gameState: StateFlow<GameState>
    val score: StateFlow<Int>
    val highscore: StateFlow<Int>
    val nBack: Int

    fun setGameType(gameType: GameType)
    fun startGame()

    fun checkMatch()
}

class GameVM(
    private val userPreferencesRepository: UserPreferencesRepository
): GameViewModel, ViewModel() {
    private val _gameState = MutableStateFlow(GameState())
    override val gameState: StateFlow<GameState>
        get() = _gameState.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int>
        get() = _score

    private val _highscore = MutableStateFlow(0)
    override val highscore: StateFlow<Int>
        get() = _highscore

    // nBack is currently hardcoded
    override val nBack: Int = 2

    private var job: Job? = null  // coroutine job for the game event
    private val eventInterval: Long = 2000L  // 2000 ms (2s)

    private val nBackHelper = NBackHelper()  // Helper that generate the event array
    private var events = emptyArray<Int>()  // Array with all events
    private var audioEvents = emptyArray<Int>()

    override fun setGameType(gameType: GameType) {
        // update the gametype in the gamestate
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun startGame() {
        job?.cancel()  // Cancel any existing game loop

        _gameState.value = GameState(
            gameType = _gameState.value.gameType,  // Behåller vald speltyp
            eventValue = -1,  // Återställ eventvärde
            currentAudio = null,  // Rensar eventuellt ljudstimuli
            indexValue = 0  // Startar index på 0
        )

        job = viewModelScope.launch {
            when (_gameState.value.gameType) {
                GameType.Audio -> {
                    events = nBackHelper.generateNBackString(10, 9, 0, nBack).toList().toTypedArray()
                    Log.d("GameVM", "The following sequence was generated for AUDIO: ${events.contentToString()}")
                    Log.d("GameVM", "Calling runAudioGame with events: ${events.contentToString()}")
                    runAudioGame(events)
                }
                GameType.Visual -> {
                    events = nBackHelper.generateNBackString(10, 9, 0, nBack).toList().toTypedArray()
                    Log.d("GameVM", "The following sequence was generated for VISUAL: ${events.contentToString()}")
                    runVisualGame(events)
                }
                GameType.AudioVisual -> {
                    //events = nBackHelper.generateNBackString(10, 5, 15, nBack).toList().toTypedArray()
                    //Log.d("GameVM", "The following sequence was generated: ${events.contentToString()}")

                    //runAudioVisualGame(events)
                }
            }
            // Todo: update the highscore
        }
    }

    override fun checkMatch() {
        /**
         * Todo: This function should check if there is a match when the user presses a match button
         * Make sure the user can only register a match once for each event.
         */
        val currentIndex = _gameState.value.eventValue
        if (currentIndex >= nBack && currentIndex < events.size) {
            val isMatch = events[currentIndex] == events[currentIndex - nBack]

            if (isMatch) {
                _score.value += 1
                Log.d("GameVM", "Match found! Score updated: ${_score.value}")
            } else {
                Log.d("GameVM", "No match found.")
            }
        }
    }
    private suspend fun runAudioGame(events: Array<Int>) {
        // Todo: Make work for Basic grade
        Log.d("GameVM", "runAudioGame started with events: ${events.contentToString()}")
        for (value in events) {
            delay(eventInterval)
            val audioValue = (value - 1 + 'A'.code).toChar().toString()
            Log.d("GameVM", "Setting currentAudio to: $audioValue")
            _gameState.value = _gameState.value.copy(currentAudio = audioValue, eventValue = -1)
            Log.d("GameVM", "Updated currentAudio to: $audioValue")
        }
    }

    private suspend fun runVisualGame(events: Array<Int>){
        // Todo: Replace this code for actual game code
        for (value in events) {
            _gameState.value = _gameState.value.copy(eventValue = value)
            delay(eventInterval)
        }

    }

    private fun runAudioVisualGame(){
        // Todo: Make work for Higher grade
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as GameApplication)
                GameVM(application.userPreferencesRespository)
            }
        }
    }

    init {
        // Code that runs during creation of the vm
        viewModelScope.launch {
            userPreferencesRepository.highscore.collect {
                _highscore.value = it
            }
        }
    }
}

// Class with the different game types
enum class GameType{
    Audio,
    Visual,
    AudioVisual
}

data class GameState(
    // You can use this state to push values from the VM to your UI.
    val gameType: GameType = GameType.Visual,  // Type of the game
    val eventValue: Int = -1,  // The value of the array string
    val currentAudio: String? = null,
    val indexValue: Int = 0
)

class FakeVM: GameViewModel{
    override val gameState: StateFlow<GameState>
        get() = MutableStateFlow(GameState()).asStateFlow()
    override val score: StateFlow<Int>
        get() = MutableStateFlow(2).asStateFlow()
    override val highscore: StateFlow<Int>
        get() = MutableStateFlow(42).asStateFlow()
    override val nBack: Int
        get() = 2

    override fun setGameType(gameType: GameType) {
    }

    override fun startGame() {
    }

    override fun checkMatch() {
    }
}