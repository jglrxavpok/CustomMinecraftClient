package org.jglrxavpok.mcclient.input

import org.jglrxavpok.mcclient.Game
import org.jglrxavpok.mcclient.rendering.GameRenderer
import org.jglrxavpok.mcclient.rendering.WorldRenderer
import org.joml.Vector2d
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

object Input {

    private var screenshotRequested = false
    private var forward = false
    private var backwards = false
    private var strafeLeft = false
    private var strafeRight = false
    private var goUp = false
    private var goDown = false
    private var mousePos = Vector2d()
    private var deltaMouse = Vector2d()
    internal var yaw = 0f
    internal var pitch = 0f

    fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        val speed = 1f
        val pressed = action == GLFW.GLFW_PRESS
        val released = action == GLFW.GLFW_RELEASE

        if(key == GLFW.GLFW_KEY_W) {
            if(pressed) forward = true
            if(released) forward = false
        }
        if(key == GLFW.GLFW_KEY_A) {
            if(pressed) strafeLeft = true
            if(released) strafeLeft = false
        }
        if(key == GLFW.GLFW_KEY_D) {
            if(pressed) strafeRight = true
            if(released) strafeRight = false
        }
        if(key == GLFW.GLFW_KEY_S) {
            if(pressed) backwards = true
            if(released) backwards = false
        }
        if(key == GLFW.GLFW_KEY_SPACE) {
            if(pressed) goUp = true
            if(released) goUp = false
        }
        if(key == GLFW.GLFW_KEY_LEFT_SHIFT) {
            if(pressed) goDown = true
            if(released) goDown = false
        }
        if(key == GLFW.GLFW_KEY_F2) {
            if(pressed) screenshotRequested = true
        }


        if(key == GLFW.GLFW_KEY_R && action == GLFW.GLFW_PRESS) {
            WorldRenderer.chunkRenderer.forceRerender()
        }

        if(key == GLFW.GLFW_KEY_ESCAPE) {
            Game.stop()
        }
    }

    fun mousePosCallback(window: Long, x: Double, y: Double) {
        val dx = mousePos.x - x;
        val dy = mousePos.y - y;
        mousePos.set(x, y)
        deltaMouse.add(dx, dy)
    }

    fun handle(deltaTime: Double) {
        val sensitivity = 0.05f

        val dx = deltaMouse.x*deltaTime
        val dy = deltaMouse.y*deltaTime
        deltaMouse.set(0.0)
        pitch += dy.toFloat()*sensitivity
        yaw += dx.toFloat()*sensitivity
        WorldRenderer.camera.rotation
                .identity()
                .rotateY(yaw)
                .rotateX(pitch)

        val speed = 10f*deltaTime.toFloat()
        val direction = Vector3f()
        if(forward) direction.add(0f, 0f, -speed)
        if(backwards) direction.add(0f, 0f, speed)
        if(strafeRight) direction.add(speed, 0f, 0f)
        if(strafeLeft) direction.add(-speed, 0f, 0f)
        if(goUp) direction.add(0f, speed, 0f)
        if(goDown) direction.add(0f, -speed, 0f)

        direction.rotateY(yaw)
        WorldRenderer.camera.position.add(direction)

        if(screenshotRequested) {
            GameRenderer.screenshot()
            screenshotRequested = false
        }
    }
}