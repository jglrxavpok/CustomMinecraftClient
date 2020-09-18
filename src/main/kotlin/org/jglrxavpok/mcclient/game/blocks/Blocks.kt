package org.jglrxavpok.mcclient.game.blocks

import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.rendering.models.BlockStateModel

// TODO: autogen
enum class Blocks(id: String, defaultBlockStateStr: String = "") {
    Air("air"),
    Stone("stone"),
    Dirt("dirt"),
    Grass("grass_block", "snowy=false"),
    ;

    val defaultState = BlockState(defaultBlockStateStr)
    val identifier: Identifier = Identifier(id)
    val model by lazy { BlockStateModel(Identifier(identifier.domain, "blockstates/${identifier.path}")) }

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