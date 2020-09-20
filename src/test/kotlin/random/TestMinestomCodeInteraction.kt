package random

import net.minestom.server.instance.block.Block
import org.jglrxavpok.mcclient.game.blocks.asBlockState

fun main() {
    for(b in Block.values()) {
        for(alt in b.alternatives) {
            println(alt.asBlockState())
        }
    }
}