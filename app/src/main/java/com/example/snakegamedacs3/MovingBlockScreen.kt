package com.example.snakegamedacs3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun MovingBlockScreen() {
    var blockPosition by remember { mutableStateOf(Pair(0f, 0f)) }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val blockSize = 50.dp
    val movementSpeed = 50f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        // The moving block
        Box(
            modifier = Modifier
                .offset(
                    x = blockPosition.first.dp,
                    y = blockPosition.second.dp
                )
                .size(blockSize)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
    }

    // Handle direction changes
    LaunchedEffect(Unit) {
        snapshotFlow { Direction.values() }
            .collect { direction ->
                when (direction) {
                    Direction.UP -> {
                        if (blockPosition.second > 0) {
                            blockPosition = blockPosition.copy(second = blockPosition.second - movementSpeed)
                        }
                    }
                    Direction.DOWN -> {
                        if (blockPosition.second < screenHeight - blockSize.value) {
                            blockPosition = blockPosition.copy(second = blockPosition.second + movementSpeed)
                        }
                    }
                    Direction.LEFT -> {
                        if (blockPosition.first > 0) {
                            blockPosition = blockPosition.copy(first = blockPosition.first - movementSpeed)
                        }
                    }
                    Direction.RIGHT -> {
                        if (blockPosition.first < screenWidth - blockSize.value) {
                            blockPosition = blockPosition.copy(first = blockPosition.first + movementSpeed)
                        }
                    }
                }
            }
    }
} 