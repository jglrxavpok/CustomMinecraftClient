package org.jglrxavpok.mcclient.network

import io.netty.buffer.ByteBuf
import io.netty.util.CharsetUtil
import org.jglrxavpok.mcclient.network.data.VarInt
import org.jglrxavpok.mcclient.network.data.VarLong

fun ByteBuf.writeVarInt(value: Int): ByteBuf {
    VarInt(value).write(this)
    return this
}

fun ByteBuf.readVarInt(): Int {
    return VarInt.readFrom(this).value
}

fun ByteBuf.writeVarLong(value: Long): ByteBuf {
    VarLong(value).write(this)
    return this
}

fun ByteBuf.readVarLong(): Long {
    return VarLong.readFrom(this).value
}

fun ByteBuf.writeUTF(value: String): ByteBuf {
    val bytes = value.toByteArray(CharsetUtil.UTF_8)
    writeVarInt(bytes.size)
    writeBytes(bytes)
    return this
}

fun ByteBuf.readUTF(): String {
    val length = readVarInt()
    val bytes = ByteArray(length)
    readBytes(bytes)
    return String(bytes, CharsetUtil.UTF_8)
}