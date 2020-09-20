package org.jglrxavpok.mcclient.rendering

import org.jglrxavpok.mcclient.Game
import org.jglrxavpok.mcclient.input.Input
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.opengl.GL11.*
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.LinkedTransferQueue
import javax.imageio.ImageIO

object GameRenderer {

    val WIDTH = 800
    val HEIGHT = 450
    private var glfwWindow: Long = -1
    private var nextFrameActions = LinkedTransferQueue<() -> Unit>()

    fun init() {
        if(!glfwInit())
            error("Failed to init GLFW")
        glfwDefaultWindowHints()
        glfwWindow = glfwCreateWindow(WIDTH, HEIGHT, "Custom Minecraft Client", NULL, NULL)
        glfwMakeContextCurrent(glfwWindow)

        GL.createCapabilities()
    //    GLUtil.setupDebugMessageCallback()

        // TODO: atlases, models, etc.

        glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_DISABLED)
        glfwSetKeyCallback(glfwWindow, Input::keyCallback)
        glfwSetCursorPosCallback(glfwWindow, Input::mousePosCallback)
        WorldRenderer.init()
    }

    fun loop() {
        var lastTime = glfwGetTime()
        while(!glfwWindowShouldClose(glfwWindow) && Game.running) {
            glfwPollEvents()
            val deltaTime = glfwGetTime()-lastTime
            lastTime = glfwGetTime()
            Input.handle(deltaTime)
            renderFrame(deltaTime)
            Game.update(deltaTime)
            glfwSwapBuffers(glfwWindow)
        }
    }

    private fun renderFrame(deltaTime: Double) {
        synchronized(nextFrameActions) {
            while(nextFrameActions.isNotEmpty()) {
                nextFrameActions.poll().invoke()
            }
        }
        // TODO: use framebuffer?
        glClearColor(0.529f, 0.808f, 0.922f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_ALPHA_TEST)

        WorldRenderer.render(Game.world)
    }

    fun nextFrame(function: () -> Unit) {
        synchronized(nextFrameActions) {
            nextFrameActions.add(function)
        }
    }

    fun screenshot() {
        val pixels = BufferUtils.createByteBuffer(WIDTH * HEIGHT*4)
        glReadPixels(0, 0, WIDTH, HEIGHT, GL_RGBA, GL_UNSIGNED_BYTE, pixels)

        val screenshot = BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB)
        for(i in 0 until pixels.capacity()/4) {
            val red = pixels[i*4+0].toInt() and 0xFF
            val green = pixels[i*4+1].toInt() and 0xFF
            val blue = pixels[i*4+2].toInt() and 0xFF
            val alpha = pixels[i*4+3].toInt() and 0xFF

            val argb = (alpha shl 24) or (red shl 16) or (green shl 8) or (blue shl 0)
            screenshot.setRGB(i% WIDTH, (HEIGHT-1)-i/WIDTH, argb)
        }
        val screenshotFile = File("screenshot_${System.currentTimeMillis()}.png")
        ImageIO.write(screenshot, "png", screenshotFile)
        println("Screenshot saved in $screenshotFile")
    }
}
