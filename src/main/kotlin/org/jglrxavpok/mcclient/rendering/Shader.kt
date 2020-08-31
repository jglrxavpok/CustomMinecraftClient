package org.jglrxavpok.mcclient.rendering

import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL20.*

class Shader(val name: String) {

    private val locations = mutableMapOf<String, Int>()
    private var program: Int = -1

    init {
        program = glCreateProgram()
        // TODO
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