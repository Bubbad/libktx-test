package com.bubba

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.TimeUtils
import ktx.app.KtxScreen
import kotlin.random.Random

class GameScreen(dropGame: DropGame) : KtxScreen {
    private var lastDropTime: Long = 0
    private var raindrops: MutableList<Rectangle>
    private val bucket: Rectangle
    private val camera: OrthographicCamera
    private val rainMusic: Music = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"))
    private val dropSound: Sound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"))
    private val bucketTexture: Texture = Texture(Gdx.files.internal("bucket.png"))
    private val dropTexture: Texture = Texture(Gdx.files.internal("drop.png"))
    private val batch: SpriteBatch = dropGame.batch
    private val font: BitmapFont = dropGame.font
    private var dropCollected = 0

    init {
        rainMusic.isLooping = true

        camera = OrthographicCamera()
        camera.setToOrtho(false, 800f, 640f)

        bucket = Rectangle()
        bucket.width = 64f
        bucket.height = 64f
        bucket.x = 800f/2
        bucket.y = 20f

        raindrops = mutableListOf()
        spawnRaindrop()
    }

    override fun show() {
        rainMusic.play()
    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()

        batch.projectionMatrix = camera.combined
        batch.begin()
        font.draw(batch, "Drops: $dropCollected", 0f, 600f)
        batch.draw(bucketTexture, bucket.x, bucket.y)
        raindrops.map { batch.draw(dropTexture, it.x, it.y) }
        batch.end()

        if(Gdx.input.isTouched) {
            val touchPoint = Vector3()
            touchPoint.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            val unprojected = camera.unproject(touchPoint)
            bucket.x = unprojected.x
            bucket.y = unprojected.y
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            bucket.x -= 500 * Gdx.graphics.deltaTime
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            bucket.x += 500 * Gdx.graphics.deltaTime
        }

        if (bucket.x < 0) {
            bucket.x = 0f
        } else if (bucket.x > 800 - 64) {
            bucket.x = 800 - 64f
        }

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
            spawnRaindrop()
        }

        raindrops.map { raindrop ->
            raindrop.y -= 200 * Gdx.graphics.deltaTime
        }

        val iter = raindrops.iterator()
        while (iter.hasNext()) {
            val raindrop = iter.next()
            raindrop.y -= 200 * Gdx.graphics.deltaTime
            if (raindrop.overlaps(bucket)) {
                dropSound.play()
                iter.remove()
                dropCollected++
            }
            if (raindrop.y + 100 < 0) iter.remove()
        }

    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
        dropTexture.dispose()
        bucketTexture.dispose()
        dropSound.dispose()
        rainMusic.dispose()
    }

    private fun spawnRaindrop() {
        val rainDrop = Rectangle()
        rainDrop.x = Random.nextInt(0, 800 - 64).toFloat()
        rainDrop.y = 640f
        rainDrop.width = 64f
        rainDrop.height = 64f
        raindrops.add(rainDrop)
        lastDropTime = TimeUtils.nanoTime()
    }
}