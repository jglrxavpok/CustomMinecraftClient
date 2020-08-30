package org.jglrxavpok.mcclient.network.data

import io.netty.buffer.ByteBuf
import java.lang.RuntimeException
import kotlin.experimental.and

inline class VarLong(val value: Long) {

    fun computeSize(): Int {
        var size = 0
        val sevenBitsMask = 0b01111111L
        var v = value and sevenBitsMask
        do {
            v = v ushr 7
            size++
        } while(v != 0L)
        return size
    }

    fun write(target: ByteBuf) {
        val sevenBitsMask = 0b01111111L
        var v = value
        do {
            var low = v and sevenBitsMask
            v = v ushr 7
            if (v != 0L) { // there is more bits
                low = low or 0b10000000
            }
            target.writeByte(low.toInt())
        } while(v != 0L)
    }

    companion object {
        fun readFrom(source: ByteBuf): VarLong {
            var index = 0
            var result = 0L
            do {
                val read = source.readByte()
                val value = (read and 0b01111111).toLong()
                result = result or (value shl (7*index))
                index++
                if(index > 10) {
                    throw RuntimeException("VarLong is too big")
                }
            } while(read.toInt() and 0b10000000 != 0)
            return VarLong(result)
        }
    }

}