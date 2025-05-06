package com.example.snakegamedacs3

import androidx.compose.ui.res.painterResource
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun lv1(level: String, onBackToHome: () -> Unit, context: Context) {
    val rows = 20
    val columns = 20
    var snake by remember { mutableStateOf(listOf(Pair(10, 10), Pair(9, 10), Pair(8, 10))) }
    var direction by remember { mutableStateOf(Direction.RIGHT) }
    var food by remember { mutableStateOf(generateFood(snake, rows, columns)) }
    var score by remember { mutableStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }

    val prefs = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE)
    var highScore by remember { mutableStateOf(prefs.getInt("high_$level", 0)) }


    val headImages = mapOf(
        Direction.UP to ImageBitmap.imageResource(context.resources, R.drawable.head_up),
        Direction.DOWN to ImageBitmap.imageResource(context.resources, R.drawable.head_down),
        Direction.LEFT to ImageBitmap.imageResource(context.resources, R.drawable.head_left),
        Direction.RIGHT to ImageBitmap.imageResource(context.resources, R.drawable.head_right)
    )
    val tailImages = mapOf(
        Direction.UP to ImageBitmap.imageResource(context.resources, R.drawable.tail_up),
        Direction.DOWN to ImageBitmap.imageResource(context.resources, R.drawable.tail_down),
        Direction.LEFT to ImageBitmap.imageResource(context.resources, R.drawable.tail_left),
        Direction.RIGHT to ImageBitmap.imageResource(context.resources, R.drawable.tail_right)
    )
    val headUp = ImageBitmap.imageResource(context.resources,R.drawable.head_up)
    val headRight = ImageBitmap.imageResource(context.resources,R.drawable.head_right)
    val headLeft = ImageBitmap.imageResource(context.resources,R.drawable.head_left)
    val headDown = ImageBitmap.imageResource(context.resources,R.drawable.head_down)

    val bodyHorizontal = ImageBitmap.imageResource(context.resources, R.drawable.body_horizontal)
    val bodyVertical = ImageBitmap.imageResource(context.resources, R.drawable.body_vertical    )
    val bodyTurnImages = mapOf(
        "top_left" to ImageBitmap.imageResource(context.resources, R.drawable.body_topleft),
        "top_right" to ImageBitmap.imageResource(context.resources, R.drawable.body_topright),
        "bottom_left" to ImageBitmap.imageResource(context.resources, R.drawable.body_bottomleft),
        "bottom_right" to ImageBitmap.imageResource(context.resources, R.drawable.body_bottomright)
    )
    val foodImage = ImageBitmap.imageResource(context.resources, R.drawable.food)

    LaunchedEffect(snake, gameOver) {
        if (gameOver) return@LaunchedEffect
        delay(200L)

        val head = snake.first()
        val newHead = when (direction) {
            Direction.UP -> Pair(head.first, (head.second - 1 + rows) % rows)
            Direction.DOWN -> Pair(head.first, (head.second + 1) % rows)
            Direction.LEFT -> Pair((head.first - 1 + columns) % columns, head.second)
            Direction.RIGHT -> Pair((head.first + 1) % columns, head.second)
        }
        if (newHead in snake) {
            gameOver = true
            if (score > highScore) {
                highScore = score
                prefs.edit().putInt("high_$level", highScore).apply()
            }
            return@LaunchedEffect
        }

        snake = if (newHead == food) {

            val mediaPlayer = MediaPlayer.create(context, R.raw.eat)

            mediaPlayer.start()

            food = generateFood(snake, rows, columns)
            score += 10
            listOf(newHead) + snake
        } else {
            listOf(newHead) + snake.dropLast(1)
        }

    }

    val background: Painter = painterResource(id = R.drawable.background_sceran)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(	0xFF003300))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text("🎯 Điểm: $score", color = Color.White, fontSize = 20.sp)
        Text("🏆 Kỷ lục: $highScore", color = Color.Yellow, fontSize = 16.sp)

        Spacer(Modifier.height(8.dp))

        Box(

            modifier = Modifier
                .aspectRatio(columns / rows.toFloat())
                .fillMaxWidth()
                .border(4.dp,Color.White)
        ) {
            Image(
                painter = background,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellWidth = (size.width / columns).toInt()
                val cellHeight = (size.height / rows).toInt()

                snake.forEachIndexed { index, (x, y) ->
                    val offset = IntOffset(x * cellWidth, y * cellHeight)
                    val size = IntSize(cellWidth, cellHeight)

                    when (index) {
                        0 -> {
                            val next = snake.getOrNull(1)
                            val dx = next?.first?.minus(x) ?: 0
                            val dy = next?.second?.minus(y) ?: 0

                            val headImages = when {
                                dx==  1 -> headLeft
                                dx== -1 ->headRight
                                dy==  1  -> headUp
                                dy== -1 -> headDown
                                dx == 1 && dy == -1 -> bodyTurnImages["bottom_left"]
                                dx == -1 && dy == -1 -> bodyTurnImages["top_left"]
                                dx == 1 && dy == 1 -> bodyTurnImages["bottom_right"]
                                dx == -1 && dy == 1 -> bodyTurnImages["top_right"]
                                else -> headImages[direction]!!
                            }

                            if (headImages != null) {

                                drawImage(
                                    image = headImages,
                                    dstOffset = offset,
                                    dstSize = size
                                )
                            }
                        }


                        snake.lastIndex -> {
                            val prev = snake[index - 1]
                            val dx = x - prev.first
                            val dy = y - prev.second
                            val tailDir = when {
                                dx == 1 -> Direction.RIGHT
                                dx == -1 -> Direction.LEFT
                                dy == 1 -> Direction.DOWN
                                else -> Direction.UP
                            }

                            drawImage(
                                image = tailImages[tailDir]!!,
                                dstOffset = offset,
                                dstSize = size
                            )
                        }

                        else -> {
                            val prev = snake[index - 1]
                            val next = snake[index + 1]
                            val dx1 = prev.first - x
                            val dy1 = prev.second - y
                            val dx2 = next.first - x
                            val dy2 = next.second - y

                            val bodyImage = when {
                                dx1 == dx2 -> bodyVertical
                                dy1 == dy2 -> bodyHorizontal
                                (dx1 == -1 && dy2 == -1) || (dx2 == -1 && dy1 == -1) -> bodyTurnImages["top_left"]
                                (dx1 == 1 && dy2 == -1) || (dx2 == 1 && dy1 == -1) -> bodyTurnImages["top_right"]
                                (dx1 == -1 && dy2 == 1) || (dx2 == -1 && dy1 == 1) -> bodyTurnImages["bottom_left"]
                                (dx1 == 1 && dy2 == 1) || (dx2 == 1 && dy1 == 1) -> bodyTurnImages["bottom_right"]
                                else -> bodyHorizontal
                            }

                            drawImage(
                                image = bodyImage!!,
                                dstOffset = offset,
                                dstSize = size
                            )
                        }
                    }
                }


                val foodOffset = IntOffset(food.first * cellWidth, food.second * cellHeight)
                drawImage(
                    image = foodImage,
                    dstOffset = foodOffset,
                    dstSize = IntSize(cellWidth, cellHeight)
                )
            }

            if (gameOver) {
                Text(
                    "💀 Game Over!",
                    color = Color.Red,
                    fontSize = 32.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    snake = listOf(Pair(10, 10), Pair(9, 10), Pair(8, 10))
                    direction = Direction.RIGHT
                    food = generateFood(snake, rows, columns)
                    score = 0
                    gameOver = false
                },
                modifier = Modifier.size(width = 150.dp, height = 60.dp)
            ) {
                Text("🔄 Chơi lại", fontSize = 18.sp)
            }

            Button(
                onClick = onBackToHome,
                modifier = Modifier.size(width = 150.dp, height = 60.dp)
            ) {
                Text("🏠 Quay về", fontSize = 18.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { if (direction != Direction.DOWN) direction = Direction.UP },
                modifier = Modifier.size(80.dp)
            ) {
                Text("⬆️", fontSize = 24.sp)
            }

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(80.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { if (direction != Direction.RIGHT) direction = Direction.LEFT },
                    modifier = Modifier.size(80.dp)
                ) {
                    Text("⬅️", fontSize = 24.sp)
                }

                Button(
                    onClick = { if (direction != Direction.LEFT) direction = Direction.RIGHT },
                    modifier = Modifier.size(80.dp)
                ) {
                    Text("➡️", fontSize = 24.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { if (direction != Direction.UP) direction = Direction.DOWN },
                modifier = Modifier.size(80.dp)
            ) {
                Text("⬇️", fontSize = 24.sp)
            }
        }
    }
}
