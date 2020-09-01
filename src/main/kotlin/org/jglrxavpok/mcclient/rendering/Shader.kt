package org.jglrxavpok.mcclient.rendering

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL20.*
import java.io.InputStreamReader

class Shader(val name: String) {

    private val locations = mutableMapOf<String, Int>()
    private var program: Int = -1

    init {
        program = glCreateProgram()
        val vertexShader = createShader("$name.vertex", GL_VERTEX_SHADER)
        val fragmentShader = createShader("$name.fragment", GL_FRAGMENT_SHADER)
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
        if(glGetProgrami(program, GL_LINK_STATUS) == 0) {
            val log = glGetProgramInfoLog(program)
            error("Failed to link shader program $name: $log")
        }
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }

    private fun createShader(name: String, type: Int): Int {
        val shader = glCreateShader(type)
        val shaderCode = InputStreamReader(javaClass.getResourceAsStream("/shaders/$name.glsl")).buffered().use { it.readText() }
        glShaderSource(shader, shaderCode)
        glCompileShader(shader)
        if(glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            val log = glGetShaderInfoLog(shader)
            error("Failed to compile shader $name: $log")
        }
        return shader
    }

    fun updateUniform(name: String, value: Vector3f) {
        checkBound()
        glUniform3f(location(name), value.x(), value.y(), value.z())
    }

    fun updateUniform(name: String, value: Matrix4f) {
        checkBound()
        val values by lazy { FloatArray(4*4) }
        value.get(values)
        glUniformMatrix4fv(location(name), false, values)
    }

    fun updateUniform(name: String, i: Int) {
        checkBound()
        glUniform1i(location(name), i)
    }

    private fun location(name: String) = glGetUniformLocation(program, name)

    fun use(action: (Shader) -> Unit) {
        val previousBound = currentlyBound
        bind()
        action(this)
        previousBound?.bind() ?: unbind()
    }

    private fun bind() {
        currentlyBound = this
        glUseProgram(program)
    }

    private fun unbind() {
        currentlyBound = null
        glUseProgram(0)
    }

    private fun checkBound() {
        if(currentlyBound != this)
            error("Shader $this is not bound")
    }

    private companion object {
        var currentlyBound: Shader? = null
    }

}