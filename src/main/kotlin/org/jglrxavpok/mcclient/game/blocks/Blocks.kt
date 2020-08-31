package org.jglrxavpok.mcclient.game.blocks

// TODO: autogen
enum class Blocks {
    Air,
    Stone;

    companion object {
        fun fromID(id: Int): Blocks {
            // TODO
            if(id == 0)
                return Air
            return Stone
        }
    }
}