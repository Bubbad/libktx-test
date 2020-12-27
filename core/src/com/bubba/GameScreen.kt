package com.bubba

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils
import ktx.app.KtxScreen
import ktx.assets.pool
import ktx.collections.iterate
import ktx.graphics.use
import ktx.log.info
import ktx.log.logger
import kotlin.random.Random

class GameScreen(private val dropGame: DropGame) : KtxScreen {
    private var lastDropTime: Long = 0
    private val rainDropsPool = pool { Rectangle() }
    private val activeRaindrops: Array<Rectangle>
    private val bucket: Rectangle
    private val camera: OrthographicCamera
    private lateinit var rainMusic: Music
    private lateinit var dropSound: Sound
    private lateinit var bucketTexture: Texture
    private lateinit var dropTexture: Texture
    private val batch: SpriteBatch = dropGame.batch
    private val font: BitmapFont = dropGame.font
    private var dropCollected = 0
    private val logger = logger<GameScreen>()

    init {
        logger.info { "Starting gamescreen" }

        camera = OrthographicCamera()
        camera.setToOrtho(false, 800f, 640f)

        bucket = Rectangle()
        bucket.width = 64f
        bucket.height = 64f
        bucket.x = 800f / 2
        bucket.y = 20f

        activeRaindrops = Array()
        spawnRaindrop()
    }

    override fun show() {
        rainMusic = dropGame.assets.get(MusicAssets.Rain).apply {
            isLooping = true
            play()
        }
        dropSound = dropGame.assets.get(SoundAssets.Drop)
        bucketTexture = dropGame.assets.get(TextureAssets.Bucket)
        dropTexture = dropGame.assets.get(TextureAssets.Drop)

    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        camera.update()

        batch.projectionMatrix = camera.combined
        batch.use { batch ->
            font.draw(batch, "Drops: $dropCollected", 0f, 600f)
            batch.draw(bucketTexture, bucket.x, bucket.y)
            activeRaindrops.map { batch.draw(dropTexture, it.x, it.y) }
        }

        if (Gdx.input.isTouched) {
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

        activeRaindrops.map { raindrop ->
            raindrop.y -= 200 * Gdx.graphics.deltaTime
        }

        activeRaindrops.iterate { raindrop, mutableIterator ->
            raindrop.y -= 200 * Gdx.graphics.deltaTime
            if (raindrop.overlaps(bucket)) {
                dropSound.play()
                mutableIterator.remove()
                rainDropsPool.free(raindrop)
                dropCollected++
            }
            if (raindrop.y + 100 < 0) {
                mutableIterator.remove()
                rainDropsPool.free(raindrop)
            }
        }

    }

    private fun spawnRaindrop() {

        val rainDrop = rainDropsPool
                .obtain()
                .set(Random.nextInt(0, 800 - 64).toFloat(), 640f, 64f, 64f)
        activeRaindrops.add(rainDrop)
        lastDropTime = TimeUtils.nanoTime()
    }
}