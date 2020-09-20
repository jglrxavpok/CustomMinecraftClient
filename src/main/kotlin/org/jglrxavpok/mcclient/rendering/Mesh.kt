package org.jglrxavpok.mcclient.rendering

import org.lwjgl.opengl.GL30.*

class Mesh(val vertexData: FloatArray, val indexData: IntArray) {

    companion object {
        val Stride = 8*4
    }

    var vao: Int = -1

    init {
        vao = glGenVertexArrays()
        glBindVertexArray(vao)
        val vertexBuffer = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer)
        glBufferData(GL_ARRAY_BUFFER, vertexData, GL_DYNAMIC_DRAW)

        val indexBuffer = glGenBuffers()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexData, GL_DYNAMIC_DRAW)

        // format
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, Stride, 0L)
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, Stride, 3*4L)
        glEnableVertexAttribArray(2)
        glVertexAttribPointer(2, 3, GL_FLOAT, false, Stride, 5*4L)

        glBindVertexArray(0)
    }

    fun render() {
        glBindVertexArray(vao)
        glDrawElements(GL_TRIANGLES, indexData.size, GL_UNSIGNED_INT, 0L)
        glBindVertexArray(0)
    }
}