package com.bubba.ecs.systems

import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.bubba.ecs.components.MoveComponent
import com.bubba.ecs.components.RenderComponent
import com.bubba.ecs.components.TransformComponent
import ktx.ashley.entity
import ktx.ashley.with
import ktx.assets.pool
import kotlin.random.Random

class RaindropSpawnSystem(private val dropTexture: Texture) : IntervalSystem(1f) {
    private val rainDropsPool = pool { Rectangle() }

    override fun updateInterval() {
        val rainDrop = rainDropsPool
                .obtain()
                .set(Random.nextInt(0, 800 - 64).toFloat(), 640f, 64f, 64f)

        engine.entity {
            this.entity.add(RenderComponent(dropTexture))
            this.entity.add(TransformComponent(rainDrop))
            this.entity.add(MoveComponent(Vector2(0f, -200f)))
        }
    }

}