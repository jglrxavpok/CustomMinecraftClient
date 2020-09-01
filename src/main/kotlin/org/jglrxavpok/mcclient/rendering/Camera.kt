package org.jglrxavpok.mcclient.rendering

import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f

class Camera {

    val position = Vector3f(0f, 0f, 0f)
    val rotation = Quaternionf().identity()
    val view = Matrix4f().identity()
    val projection = Matrix4f().setPerspective(90f, 16f/9f, 0.01f, 1000.0f)

    fun updateShader(shader: Shader) {
        val viewProj by lazy { Matrix4f().identity() }
        updateViewMatrix()
        shader.updateUniform("camera.viewProj", viewProj.set(projection).mul(view))
    }

    private fun updateViewMatrix() {
        val eyeHeight = 1.8f
        view.identity().translate(-position.x(), -(position.y()+eyeHeight), -position.z()).rotate(rotation.conjugate())
    }
}
