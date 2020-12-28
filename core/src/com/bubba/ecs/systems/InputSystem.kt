package com.bubba.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.bubba.ecs.components.MoveComponent
import ktx.ashley.allOf

class InputSystem(bucketEntity: Entity): EntitySystem() {

    private val bucketSpeed = bucketEntity.getComponent(MoveComponent::class.java).speed

    override fun update(deltaTime: Float) {
        when {
            Gdx.input.isKeyPressed(Input.Keys.LEFT) -> {
                bucketSpeed.x = -500f
            }
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> {
                bucketSpeed.x = 500f
            }
            else -> {
                bucketSpeed.x = 0f
            }
        }
    }
}