package org.jglrxavpok.mcclient.rendering

import org.jglrxavpok.mcclient.Game
import org.jglrxavpok.mcclient.input.Input
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.opengl.GL11.*

object GameRenderer {

    val WIDTH = 800
    val HEIGHT = 450
    private var glfwWindow: Long = -1
    private var running = true

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
        while(!glfwWindowShouldClose(glfwWindow) && running) {
            glfwPollEvents()
            val deltaTime = lastTime-glfwGetTime()
            lastTime = glfwGetTime()
            Input.handle(deltaTime)
            renderFrame(deltaTime)
            glfwSwapBuffers(glfwWindow)
        }
    }

    private fun renderFrame(deltaTime: Double) {
        // TODO: use framebuffer?
        glClearColor(0f, 0f, 0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glEnable(GL_DEPTH_TEST)

        WorldRenderer.render(Game.world)
    }

    fun stop() {
        running = false
    }
}
