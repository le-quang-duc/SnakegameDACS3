package com.example.snakegamedacs3

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VoiceControlActivity : ComponentActivity() {
    private var voiceCommands: VoiceCommands? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startVoiceControl()
        } else {
            Toast.makeText(this, "Cần quyền truy cập micro để sử dụng điều khiển giọng nói", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissionAndStart()
    }

    private fun checkPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startVoiceControl()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    private fun startVoiceControl() {
        voiceCommands = VoiceCommands(this)
        setContent {
            GameScreen(voiceCommands!!)
        }
        voiceCommands?.startListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceCommands?.destroy()
    }
}

@Composable
fun GameScreen(voiceCommands: VoiceCommands) {
    var currentLevel by remember { mutableStateOf(1) }
    var isPlaying by remember { mutableStateOf(false) }
    val currentCommand by voiceCommands.currentCommand.collectAsState()
    val direction by voiceCommands.direction.collectAsState()
    val gameAction by voiceCommands.gameAction.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(gameAction) {
        when (gameAction) {
            VoiceCommands.GameAction.START_GAME -> {
                isPlaying = true
                Toast.makeText(context, "Bắt đầu chơi!", Toast.LENGTH_SHORT).show()
            }
            VoiceCommands.GameAction.RESTART_GAME -> {
                isPlaying = true
                Toast.makeText(context, "Chơi lại!", Toast.LENGTH_SHORT).show()
            }
            VoiceCommands.GameAction.GO_BACK -> {
                isPlaying = false
                Toast.makeText(context, "Quay lại menu", Toast.LENGTH_SHORT).show()
            }
            VoiceCommands.GameAction.SELECT_LEVEL_1 -> {
                currentLevel = 1
                Toast.makeText(context, "Chọn màn 1", Toast.LENGTH_SHORT).show()
            }
            VoiceCommands.GameAction.SELECT_LEVEL_2 -> {
                currentLevel = 2
                Toast.makeText(context, "Chọn màn 2", Toast.LENGTH_SHORT).show()
            }
            VoiceCommands.GameAction.SELECT_LEVEL_3 -> {
                currentLevel = 3
                Toast.makeText(context, "Chọn màn 3", Toast.LENGTH_SHORT).show()
            }
            null -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Màn chơi hiện tại: $currentLevel",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Trạng thái: ${if (isPlaying) "Đang chơi" else "Chưa bắt đầu"}",
            style = MaterialTheme.typography.bodyLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Lệnh hiện tại: $currentCommand",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        when {
            isPlaying -> {
                when (currentLevel) {
                    1 -> Level1()
                    2 -> Level2()
                    3 -> Level3()
                }
            }
            else -> {
                Text(
                    text = "Nói 'bắt đầu' để chơi\nNói 'màn một/hai/ba' để chọn màn",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
} 