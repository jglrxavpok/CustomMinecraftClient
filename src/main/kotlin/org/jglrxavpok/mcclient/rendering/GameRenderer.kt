package org.jglrxavpok.mcclient.rendering

import org.jglrxavpok.mcclient.Game
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GLUtil

object GameRenderer {

    val WIDTH = 800
    val HEIGHT = 450
    private var glfwWindow: Long = -1

    fun init() {
        if(!glfwInit())
            error("Failed to init GLFW")
        glfwDefaultWindowHints()
        glfwWindow = glfwCreateWindow(WIDTH, HEIGHT, "Custom Minecraft Client", NULL, NULL)
        glfwMakeContextCurrent(glfwWindow)

        GL.createCapabilities()
        GLUtil.setupDebugMessageCallback()

        WorldRenderer.init()
    }

    fun loop() {
        while(!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents()
            renderFrame()
            glfwSwapBuffers(glfwWindow)
        }
    }

    private fun renderFrame() {
        // TODO: use framebuffer?
        glClearColor(0f, 0f, 0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        WorldRenderer.render(Game.world)
    }
}