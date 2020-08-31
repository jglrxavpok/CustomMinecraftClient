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
        updateViewMatrix()
        shader.updateUniform("camera.view", view)
        shader.updateUniform("camera.projection", projection)
    }

    private fun updateViewMatrix() {
        view.identity().translate(-position.x(), -position.y(), -position.z()).rotate(rotation.conjugate())
    }
}
