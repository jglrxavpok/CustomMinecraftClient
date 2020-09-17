package org.jglrxavpok.mcclient.rendering.atlases

import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.rendering.Texture
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImageOp
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.sqrt

class Atlas(private val images: Map<Identifier, () -> BufferedImage>) {

    private var built = false
    private var texture: Texture? = null
    private val sprites = hashMapOf<Identifier, AtlasSprite>()

    init {
        buildTexture()
    }

    fun getSprite(id: Identifier): AtlasSprite {
        if(!built) error("Atlas not built yet")
        return sprites[id] ?: TODO("Handle missing/unknown sprites")
    }

    fun getTexture(): Texture {
        if(!built) error("Atlas not built yet")
        return texture!!
    }

    fun bind() {
        if(!built) error("Atlas not built yet")
        texture!!.bind()
    }

    fun buildTexture(): Atlas {
        var init = false

        var spriteWidth = -1
        var spriteHeight = -1
        var totalWidth = -1
        var totalHeight = -1
        var atlasImage: BufferedImage? = null
        var atlasGraphics: Graphics2D? = null

        val spritesPerLine = ceil(sqrt(images.size.toFloat())).toInt()
        val lines = ceil(images.size.toFloat()/spritesPerLine).toInt()

        var index = 0
        for((id, imageSupplier) in images) {
            val image = imageSupplier()

            if(!init) {
                spriteWidth = image.width
                spriteHeight = image.height

                totalWidth = spritesPerLine*spriteWidth
                totalHeight = spritesPerLine*spriteWidth
                atlasImage = BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB)
                atlasGraphics = atlasImage.createGraphics()
                init = true
            }

            val startX = (index % spritesPerLine) * spriteWidth
            val startY = (index / spritesPerLine) * spriteHeight
            atlasGraphics!!.drawImage(image, startX, startY, null)

            val sprite = AtlasSprite(
                    (startX.toFloat()+0.5f)/totalWidth, (startY.toFloat()+0.5f)/totalHeight,
                    (startX.toFloat()+spriteWidth)/totalWidth, (startY.toFloat()+spriteHeight)/totalHeight
            )
            sprites += id to sprite

            index++
        }

        // TODO: debug only
        ImageIO.write(atlasImage, "png", File("test_atlas.png"))

        texture = Texture(atlasImage!!)
        built = true
        return this
    }

}