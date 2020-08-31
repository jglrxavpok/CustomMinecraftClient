package org.jglrxavpok.mcclient.game

import io.netty.buffer.ByteBuf
import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.network.data.DataType

class NetworkTag(val identifier: Identifier, val entries: IntArray) {

    companion object {
        fun read(buffer: ByteBuf): NetworkTag {
            val id = DataType.Identifier.read(buffer) as Identifier
            val count = DataType.VarInt.read(buffer) as Int
            val entries = IntArray(count)
            for(i in 0 until count) {
                entries[i] = DataType.VarInt.read(buffer) as Int
            }
            return NetworkTag(id, entries)
        }
    }

    fun write(buffer: ByteBuf) {
        DataType.Identifier.write(buffer, identifier)
        DataType.VarInt.write(buffer, entries.size)
        for(id in entries) {
            DataType.VarInt.write(buffer, id)
        }
    }

}