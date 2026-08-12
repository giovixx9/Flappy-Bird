package com.giovixx9.flappybird

import org.junit.Test
import androidx.compose.ui.graphics.Color
import com.giovixx9.flappybird.ui.screens.GameState

class GameStateTest {
    @Test
    fun testGameStateInitialization() {
        val gameState = GameState()
        assert(gameState.score == 0)
        assert(gameState.gameOver == false)
        assert(gameState.birdY == 400f)
    }

    @Test
    fun testJump() {
        val gameState = GameState()
        val jumpState = gameState.jump()
        assert(jumpState.birdVelocity == -8f)
    }

    @Test
    fun testGameOver() {
        val gameState = GameState(birdY = 800f)
        val updatedState = gameState.update()
        assert(updatedState.gameOver == true)
    }
}
