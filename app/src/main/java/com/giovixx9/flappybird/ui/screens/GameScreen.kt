package com.giovixx9.flappybird.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giovixx9.flappybird.ui.theme.BirdYellow
import com.giovixx9.flappybird.ui.theme.PipeGreen
import com.giovixx9.flappybird.ui.theme.SkyBlue
import kotlinx.coroutines.delay

@Composable
fun GameScreen() {
    val gameState = remember { mutableStateOf(GameState()) }
    val screenHeight = 800.dp
    val screenWidth = 400.dp

    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            gameState.value = gameState.value.update()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue)
            .clickable {
                gameState.value = gameState.value.jump()
            }
    ) {
        // Score
        Text(
            text = "Score: ${gameState.value.score}",
            modifier = Modifier.align(Alignment.TopCenter),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Bird
        Box(
            modifier = Modifier
                .offset(y = gameState.value.birdY.dp)
                .size(30.dp)
                .align(Alignment.CenterStart)
                .background(BirdYellow, CircleShape)
        )

        // Pipes
        for (pipe in gameState.value.pipes) {
            PipeComponent(pipe, screenHeight)
        }

        // Game Over Screen
        if (gameState.value.gameOver) {
            GameOverScreen(gameState.value.score) {
                gameState.value = GameState()
            }
        }
    }
}

@Composable
fun PipeComponent(pipe: Pipe, screenHeight: Dp) {
    // Top Pipe
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = pipe.topHeight.dp)
            .offset(x = pipe.x.dp, y = 0.dp)
            .background(PipeGreen)
    )

    // Gap
    Spacer(modifier = Modifier.height((pipe.gapSize).dp))

    // Bottom Pipe
    Box(
        modifier = Modifier
            .size(width = 60.dp, height = pipe.bottomHeight.dp)
            .offset(x = pipe.x.dp, y = (pipe.topHeight + pipe.gapSize).dp)
            .background(PipeGreen)
    )
}

@Composable
fun GameOverScreen(score: Int, onRestart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GAME OVER",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Final Score: $score",
                fontSize = 32.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "TAP TO RESTART",
                fontSize = 20.sp,
                color = Color.Yellow,
                modifier = Modifier.clickable { onRestart() }
            )
        }
    }
}

data class Pipe(
    val x: Float,
    val topHeight: Float,
    val bottomHeight: Float,
    val gapSize: Float = 150f
)

data class GameState(
    val birdY: Float = 400f,
    val birdVelocity: Float = 0f,
    val pipes: List<Pipe> = generatePipes(),
    val score: Int = 0,
    val gameOver: Boolean = false
) {
    fun update(): GameState {
        if (gameOver) return this

        val newVelocity = birdVelocity + 0.5f
        val newBirdY = birdY + newVelocity

        // Check collision with ground or ceiling
        if (newBirdY > 750 || newBirdY < 0) {
            return this.copy(gameOver = true)
        }

        // Move pipes
        val updatedPipes = pipes.map { it.copy(x = it.x - 5) }.toMutableList()

        // Remove pipes that went off screen
        updatedPipes.removeAll { it.x < -60 }

        // Add new pipe when needed
        if (updatedPipes.last().x < 200) {
            updatedPipes.add(generateRandomPipe())
        }

        // Check collision with pipes
        var newScore = score
        for (pipe in updatedPipes) {
            if (newBirdY in (pipe.topHeight)..(pipe.topHeight + pipe.gapSize) &&
                30f in (pipe.x)..(pipe.x + 60)
            ) {
                return this.copy(gameOver = true)
            }
            if (pipe.x == 50f) {
                newScore = score + 1
            }
        }

        return this.copy(
            birdY = newBirdY,
            birdVelocity = newVelocity,
            pipes = updatedPipes,
            score = newScore
        )
    }

    fun jump(): GameState {
        return this.copy(birdVelocity = -8f)
    }
}

fun generatePipes(): List<Pipe> {
    return listOf(
        Pipe(400f, 150f, 400f),
        Pipe(600f, 200f, 400f),
        Pipe(800f, 100f, 400f)
    )
}

fun generateRandomPipe(): Pipe {
    val topHeight = (50..300).random().toFloat()
    val gapSize = 150f
    val bottomHeight = 800f - topHeight - gapSize
    return Pipe(800f, topHeight, bottomHeight)
}
