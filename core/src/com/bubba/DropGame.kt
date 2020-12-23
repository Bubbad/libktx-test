package com.bubba

import com.badlogic.gdx.Game
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class DropGame : Game() {
    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont

    private lateinit var gameScreen: Screen

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
        font.data.setScale(1.5f)
        gameScreen = GameScreen(this)
        this.setScreen(gameScreen)
    }

    override fun dispose() {
        batch.dispose()
        gameScreen.dispose()
    }
}