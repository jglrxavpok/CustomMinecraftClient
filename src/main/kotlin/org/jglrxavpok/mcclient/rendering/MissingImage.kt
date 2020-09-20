package org.jglrxavpok.mcclient.rendering

import java.awt.image.BufferedImage

object MissingImage: BufferedImage(16,16, TYPE_INT_ARGB) {

    init {
        for (j in 0..15) {
            for (i in 0..15) {
                val black = (i - 7) * (j - 7) >= 0
                if(black) {
                    setRGB(i, j, 0xFF000000.toInt())
                } else {
                    setRGB(i, j, 0xFF800080.toInt())
                }
            }
        }
    }
}