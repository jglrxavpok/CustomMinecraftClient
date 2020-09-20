package org.jglrxavpok.mcclient.game.blocks

import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockAlternative
import org.jglrxavpok.mcclient.BlockStateID
import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.rendering.models.BlockStateModel

private var models = mutableMapOf<Block, BlockStateModel>()
private var defaultStates = mutableMapOf<Block, BlockState>()

fun Block.getModel() = models.computeIfAbsent(this) {
    val identifier = Identifier(this.getName())
    BlockStateModel(Identifier(identifier.domain, "blockstates/${identifier.path}"))
}

fun Block.getDefaultState(): BlockState {
    return defaultStates.computeIfAbsent(this) { getAlternative(blockId).asBlockState() }
}

fun BlockAlternative.asBlockState(): BlockState {
    val v by lazy { BlockState(this) }
    return v
}

class BlockState(val id: BlockStateID, val properties: Map<String, String>) {

    private val propertyAsStringKey: String = properties.entries
        .sortedBy { it.key }
        .joinToString(",") { "${it.key}=${it.value}" }

    // to convert from Minestom code
    constructor(alternative: BlockAlternative): this(alternative.id, alternative.createPropertiesMap()) {
        println(alternative) // TODO: remove
    }

    // TODO

    fun toStringKey(): String {
        return propertyAsStringKey
    }

    override fun toString(): String {
        return "BlockState{${toStringKey()}}"
    }
}
