package org.jglrxavpok.mcclient.rendering

import org.jglrxavpok.mcclient.Identifier
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL13.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class Texture(val image: BufferedImage) {

    private var id: Int = -1
    // TODO
    constructor(name: Identifier): this(ImageIO.read(Texture::class.java.getResource("none")))

    init {
        id = glGenTextures()
        bind()
        val width = image.width
        val height = image.height

        val pixels = BufferUtils.createByteBuffer(4*width*height)
        for(y in 0 until height) {
            for(x in 0 until width) {
                val pixel = image.getRGB(x, y)
                val red = (pixel shr 16) and 0xFF
                val blue = (pixel shr 8) and 0xFF
                val green = (pixel shr 0) and 0xFF
                val alpha = (pixel shr 24) and 0xFF
                pixels.put(red.toByte())
                pixels.put(blue.toByte())
                pixels.put(green.toByte())
                pixels.put(alpha.toByte())
            }
        }
        pixels.rewind()
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    }

    fun bind(textureUnit: Int = 0) {
        glActiveTexture(GL_TEXTURE0+textureUnit)
        glBindTexture(GL_TEXTURE_2D, id)
    }
}