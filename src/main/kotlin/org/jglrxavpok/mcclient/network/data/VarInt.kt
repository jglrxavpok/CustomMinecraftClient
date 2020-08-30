package org.jglrxavpok.mcclient.network.data

import io.netty.buffer.ByteBuf
import java.io.OutputStream
import java.lang.RuntimeException
import kotlin.experimental.and

inline class VarInt(val value: Int) {

    fun computeSize(): Int {
        var size = 0
        val sevenBitsMask = 0b01111111
        var v = value and sevenBitsMask
        do {
            v = v ushr 7
            size++
        } while(v != 0)
        return size
    }

    fun write(target: ByteBuf) {
        write {
            target.writeByte(it)
        }
    }

    private fun write(action: (Int) -> Unit) {
        val sevenBitsMask = 0b01111111
        var v = value
        do {
            var low = v and sevenBitsMask
            v = v ushr 7
            if (v != 0) { // there is more bits
                low = low or 0b10000000
            }
            action(low)
        } while(v != 0)
    }

    fun write(target: OutputStream) {
        write {
            target.write(it)
        }
    }

    companion object {
        fun readFrom(source: ByteBuf): VarInt {
            var index = 0
            var result = 0
            do {
                val read = source.readByte()
                val value = (read and 0b01111111).toInt()
                result = result or (value shl (7*index))
                index++
                if(index > 5) {
                    throw RuntimeException("VarInt is too big")
                }
            } while(read.toInt() and 0b10000000 != 0)
            return VarInt(result)
        }
    }

}