package com.example.snakegamedacs3

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snakegamedacs3.ui.theme.SnakegameDACS3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SnakegameDACS3Theme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf("HOME") }
    var selectedLevel by remember { mutableStateOf("1") }
    val context = LocalContext.current

    when (currentScreen) {
        "HOME" -> HomeScreen(
            selectedLevel = selectedLevel,
            onLevelChange = { selectedLevel = it },
            onPlay = { currentScreen = "LEVEL" },
            context = context
        )
        "LEVEL" -> when (selectedLevel) {
            "1" -> lv1(level = selectedLevel, onBackToHome = { currentScreen = "HOME" }, context = context)
            "2" -> lv2(level = selectedLevel, onBackToHome = { currentScreen = "HOME" }, context = context)
            "3" -> lv3(level = selectedLevel, onBackToHome = { currentScreen = "HOME" }, context = context)
            else -> currentScreen = "HOME"
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    selectedLevel: String,
    onLevelChange: (String) -> Unit,
    onPlay: () -> Unit,
    context: Context
) {
    val prefs = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🐍 Snake Game", fontSize = 36.sp, color = Color.Green)

        Spacer(Modifier.height(32.dp))

        Text("Chọn cấp độ:", color = Color.White)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4CAF50))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        val previousLevel = when (selectedLevel) {
                            "2" -> "1"
                            "3" -> "2"
                            else -> "3"
                        }
                        onLevelChange(previousLevel)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Previous Level",
                        tint = Color.Black
                    )
                }

                Spacer(Modifier.width(8.dp))

                AnimatedContent(
                    targetState = selectedLevel,
                    transitionSpec = {
                        slideInHorizontally { it } + fadeIn() with
                                slideOutHorizontally { -it } + fadeOut()
                    },
                    label = "LevelTransition"
                ) { level ->
                    Text(
                        text = "level$level",
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                }

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        val nextLevel = when (selectedLevel) {
                            "1" -> "2"
                            "2" -> "3"
                            else -> "1"
                        }
                        onLevelChange(nextLevel)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Next Level",
                        tint = Color.Black
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = onPlay) {
            Text("▶️ Bắt đầu chơi", fontSize = 18.sp)
        }

        Spacer(Modifier.height(24.dp))

        Text("📈 Lịch sử điểm cao", color = Color.Yellow, fontSize = 20.sp)
        listOf("1", "2", "3").forEach {
            val score = prefs.getInt("high_$it", 0)
            Text("🎮 Level $it: $score điểm", color = Color.White)
        }
    }
}
