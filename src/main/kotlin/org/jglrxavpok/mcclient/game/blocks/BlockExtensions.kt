package org.jglrxavpok.mcclient.game.blocks

import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.BlockAlternative
import org.jglrxavpok.mcclient.BlockStateID
import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.world.World
import org.jglrxavpok.mcclient.rendering.models.BlockStateModel
import org.joml.Vector3f
import java.util.concurrent.ConcurrentHashMap

private var models = ConcurrentHashMap<Block, BlockStateModel>()
private var defaultStates = ConcurrentHashMap<Block, BlockState>()
private var ids = ConcurrentHashMap<Block, Identifier>()
private var stateConversions = ConcurrentHashMap<BlockAlternative, BlockState>()

fun Block.getIdentifier() = ids.computeIfAbsent(this) {
    Identifier(getName())
}

fun Block.getModel() = models.computeIfAbsent(this) {
    val identifier = Identifier(this.getName())
    BlockStateModel(Identifier(identifier.domain, "blockstates/${identifier.path}"))
}

fun Block.getDefaultState(): BlockState {
    return defaultStates.computeIfAbsent(this) { getAlternative(blockId).asBlockState() }
}

fun BlockAlternative.asBlockState(): BlockState {
    return stateConversions.computeIfAbsent(this) { BlockState(this) }
}

fun Block.hasTint(world: World, blockState: BlockState, x: Int, y: Int, z: Int) = when(this) {
    Block.ACACIA_LEAVES, Block.BIRCH_LEAVES, Block.DARK_OAK_LEAVES, Block.JUNGLE_LEAVES, Block.OAK_LEAVES, Block.SPRUCE_LEAVES,
    Block.GRASS, Block.TALL_GRASS, Block.FERN, Block.LARGE_FERN -> true
    Block.GRASS_BLOCK -> !blockState.getBoolean("snowy")
    Block.WATER -> true
    else -> false
}

fun Block.fillTint(world: World, blockState: BlockState, x: Int, y: Int, z: Int, colorDestination: Vector3f): Unit {
    when(this) {
        Block.ACACIA_LEAVES, Block.BIRCH_LEAVES, Block.DARK_OAK_LEAVES, Block.JUNGLE_LEAVES, Block.OAK_LEAVES, Block.SPRUCE_LEAVES,
        Block.GRASS, Block.TALL_GRASS, Block.FERN, Block.LARGE_FERN -> {
            // TODO: based on biome
            colorDestination.set(0x77/256f, 0xab/256f, 0x2f/256f)
        }
        Block.GRASS_BLOCK -> {
            // TODO: based on biome
            colorDestination.set(0x91/256f, 0xbd/256f, 0x59/256f)
        }
        Block.WATER -> {
            // TODO: based on biome
            val r = 0x3F/256f
            val g = 0x76/256f
            val b = 0xE4/256f
            colorDestination.set(r, g, b)
        }
    }
}

class BlockState(val id: BlockStateID, val properties: Map<String, String>) {

    private val propertyAsStringKey: String = properties.entries
        .sortedBy { it.key }
        .joinToString(",") { "${it.key}=${it.value}" }

    /**
     * To convert from Minestom code
     */
    constructor(alternative: BlockAlternative): this(alternative.id, alternative.createPropertiesMap()) {
        println(alternative) // TODO: remove
    }

    fun get(name: String): String {
        return properties[name] ?:
        error("Unknown block state property: $name")
    }

    fun getBoolean(name: String) = get(name).toBoolean()

    /**
     * Returns a string representation used as a key, especially in blockstate models
     */
    fun toStringKey(): String {
        return propertyAsStringKey
    }

    override fun toString(): String {
        return "BlockState{${toStringKey()}}"
    }
}
