package org.jglrxavpok.mcclient.rendering

import org.lwjgl.opengl.GL11.*

enum class RenderPass(private val start: () -> Unit, private val end: () -> Unit) {
    SOLID({

    }, {

    }),
    TRANSLUCENT({
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    }, {
        glDisable(GL_BLEND)
    }),

    ;

    fun use(block: (RenderPass) -> Unit) {
        start()
        try {
            block(this)
        } finally {
            end()
        }
    }
}