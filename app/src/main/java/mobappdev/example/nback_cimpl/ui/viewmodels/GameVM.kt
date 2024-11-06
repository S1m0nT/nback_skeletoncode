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

    fun checkMatchVisual()
    fun checkMatchAudio()
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
    override val nBack: Int = 1

    private var job: Job? = null  // coroutine job for the game event
    private val eventInterval: Long = 2000L  // 2000 ms (2s)

    private val nBackHelper = NBackHelper()  // Helper that generate the event array
    private var events = emptyArray<Int>()  // Array with all events
    private var audioEvents = emptyArray<Int>()  // Array with all events

    override fun setGameType(gameType: GameType) {
        // update the gametype in the gamestate
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun startGame() {
        job?.cancel()  // Cancel any existing game loop

        _gameState.value = GameState(gameType = _gameState.value.gameType, eventValue = -1, currentAudio = null, indexValue = 0, eventNumber = 0)

        job = viewModelScope.launch {
            when (_gameState.value.gameType) {
                GameType.Audio -> {
                    audioEvents = nBackHelper.generateNBackString(10, 9, 30, nBack).toList().toTypedArray()
                    Log.d("GameVM", "The following sequence was generated for AUDIO: ${audioEvents.contentToString()}")
                    runAudioGame(audioEvents)
                }
                GameType.Visual -> {
                    events = nBackHelper.generateNBackString(10, 9, 30, nBack).toList().toTypedArray()
                    Log.d("GameVM", "The following sequence was generated for VISUAL: ${events.contentToString()}")
                    runVisualGame(events)
                }
                GameType.AudioVisual -> {
                    audioEvents = nBackHelper.generateNBackString(10, 9, 30, nBack).toList().toTypedArray()
                    events = nBackHelper.generateNBackString(10, 9, 30, nBack).toList().toTypedArray()
                    Log.d("GameVM", "Generated AUDIOVISUAL sequences: Audio - ${audioEvents.contentToString()}, Visual - ${events.contentToString()}")
                    runAudioVisualGame(audioEvents, events)
                }

            }
            if (_score.value > _highscore.value) {
                _highscore.value = _score.value
                userPreferencesRepository.saveHighScore(_score.value)
                Log.d("GameVM", "New highscore saved: ${_score.value}")
            }

        }
    }

    override fun checkMatchAudio() {
        /**
         * Todo: This function should check if there is a match when the user presses a match button
         * Make sure the user can only register a match once for each event.
         */
        if (_gameState.value.matchChecked) return

        val currentIndex = _gameState.value.audioEventValue
        if (currentIndex != null) {
            if (currentIndex >= nBack && currentIndex < audioEvents.size) {
                val isMatch = audioEvents[currentIndex] == audioEvents[currentIndex - nBack]
                _gameState.value = _gameState.value.copy(
                    matchStatus = if (isMatch) MatchStatus.Match else MatchStatus.NoMatch,
                    matchChecked = true
                )

                if (isMatch) _score.value += 1
            } else {
                _gameState.value = _gameState.value.copy(matchStatus = MatchStatus.None)
            }
        }
    }

    override fun checkMatchVisual() {
        /**
         * Todo: This function should check if there is a match when the user presses a match button
         * Make sure the user can only register a match once for each event.
         */
        if (_gameState.value.matchChecked) return

        val currentIndex = _gameState.value.eventValue
        if (currentIndex >= nBack && currentIndex < events.size) {
            val isMatch = events[currentIndex] == events[currentIndex - nBack]
            _gameState.value = _gameState.value.copy(
                matchStatus = if (isMatch) MatchStatus.Match else MatchStatus.NoMatch,
                matchChecked = true
            )

            if (isMatch) _score.value += 1
        } else {
            _gameState.value = _gameState.value.copy(matchStatus = MatchStatus.None)
        }
    }

    private suspend fun runAudioGame(events: Array<Int>, ) {
        for (value in events) {
            val audioValue = (value - 1 + 'A'.code).toChar().toString()
            _gameState.value = _gameState.value.copy(currentAudio = audioValue, audioEventValue = value, eventNumber = _gameState.value.eventNumber + 1, matchStatus = MatchStatus.None, matchChecked = false)
            delay(eventInterval)
        }
    }

    private suspend fun runVisualGame(events: Array<Int>){
        for (value in events) {
            _gameState.value = _gameState.value.copy(eventValue = value, eventNumber = _gameState.value.eventNumber + 1)
            delay(eventInterval)
        }

    }


    private suspend fun runAudioVisualGame(audioEvents: Array<Int>, visualEvents: Array<Int>) {
        delay(eventInterval)
        for (i in 0 until minOf(audioEvents.size, visualEvents.size)) {
            val audioValue = (audioEvents[i] - 1 + 'A'.code).toChar().toString()
            val visualValue = visualEvents[i]

            _gameState.value = _gameState.value.copy(currentAudio = audioValue, audioEventValue = audioEvents[i],  eventValue = visualValue,eventNumber = _gameState.value.eventNumber + 1,  matchStatus = MatchStatus.None, matchChecked = false)
            delay(eventInterval)
        }
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

enum class MatchStatus {
    Match,
    NoMatch,
    None
}

data class GameState(
    // You can use this state to push values from the VM to your UI.
    val gameType: GameType = GameType.Visual,  // Type of the game
    val eventValue: Int = -1, // The value of the array string
    val audioEventValue: Int? = null,
    val currentAudio: String? = null,
    val indexValue: Int = 0,
    val matchStatus: MatchStatus = MatchStatus.None,
    val matchChecked: Boolean = false,
    val eventNumber: Int = 0,
    val nBack: Int = 1,
    val eventInterval: Long = 2000L,
    val totalEvents: Int = 10

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

    override fun checkMatchAudio() {
    }
    override fun checkMatchVisual() {
    }
}