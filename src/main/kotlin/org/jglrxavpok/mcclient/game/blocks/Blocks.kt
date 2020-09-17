package org.jglrxavpok.mcclient.game.blocks

// TODO: autogen
enum class Blocks {
    Air,
    Stone,
    Dirt,
    Grass,
    ;

    val defaultState = BlockState()

    companion object {
        fun fromID(id: Int): Blocks {
            // TODO
            return when(id) {
                0 -> Air
                9 -> Grass
                10 -> Dirt
                else -> Stone
            }
        }
    }
}