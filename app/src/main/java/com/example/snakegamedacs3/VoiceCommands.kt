package com.example.snakegamedacs3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class VoiceCommands(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val _currentCommand = MutableStateFlow<String>("")
    val currentCommand: StateFlow<String> = _currentCommand
    private val _direction = MutableStateFlow<Direction?>(null)
    val direction: StateFlow<Direction?> = _direction
    private val _gameAction = MutableStateFlow<GameAction?>(null)
    val gameAction: StateFlow<GameAction?> = _gameAction

    enum class GameAction {
        START_GAME,
        RESTART_GAME,
        GO_BACK,
        SELECT_LEVEL_1,
        SELECT_LEVEL_2,
        SELECT_LEVEL_3
    }

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            setupRecognitionListener()
        } else {
            Toast.makeText(context, "Thiết bị không hỗ trợ nhận dạng giọng nói", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecognitionListener() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _currentCommand.value = "Đang lắng nghe..."
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                _currentCommand.value = "Lỗi nhận dạng giọng nói"
                startListening() // Restart listening after error
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val command = matches[0].toLowerCase()
                    _currentCommand.value = command
                    processCommand(command)
                }
                startListening() // Continue listening after processing
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun processCommand(command: String) {
        when {
            // Movement commands
            command.contains("lên") || command.contains("đi lên") -> _direction.value = Direction.UP
            command.contains("xuống") || command.contains("đi xuống") -> _direction.value = Direction.DOWN
            command.contains("trái") || command.contains("qua trái") -> _direction.value = Direction.LEFT
            command.contains("phải") || command.contains("qua phải") -> _direction.value = Direction.RIGHT

            // Game control commands
            command.contains("bắt đầu") || command.contains("chơi") -> _gameAction.value = GameAction.START_GAME
            command.contains("chơi lại") || command.contains("bắt đầu lại") -> _gameAction.value = GameAction.RESTART_GAME
            command.contains("quay lại") || command.contains("trở về") -> _gameAction.value = GameAction.GO_BACK

            // Level selection commands
            command.contains("màn một") || command.contains("cấp một") -> _gameAction.value = GameAction.SELECT_LEVEL_1
            command.contains("màn hai") || command.contains("cấp hai") -> _gameAction.value = GameAction.SELECT_LEVEL_2
            command.contains("màn ba") || command.contains("cấp ba") -> _gameAction.value = GameAction.SELECT_LEVEL_3
        }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi khởi động nhận dạng giọng nói", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
} 