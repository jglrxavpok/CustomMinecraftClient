package org.jglrxavpok.mcclient.network.data

import io.netty.buffer.*
import org.jglrxavpok.hephaistos.nbt.*
import org.jglrxavpok.mcclient.network.*
import java.io.ByteArrayOutputStream

/**
 * DataTypes used to (de-)serialize packets
 */
enum class DataType(private val writingFunction: (ByteBuf, Any?) -> Unit, private val readingFunction: (ByteBuf) -> Any?) {
    Boolean(
        { buf, value -> buf.writeBoolean(value as kotlin.Boolean) },
        { buf -> buf.readBoolean() }
    ),
    Byte(
        { buf, value -> buf.writeByte((value as kotlin.Byte).toInt()) },
        { buf -> buf.readByte() }
    ),
    UnsignedByte(
        { buf, value -> buf.writeByte((value as kotlin.Byte).toInt()) },
        { buf -> buf.readByte() }
    ),
    Short(
        { buf, value -> buf.writeShort((value as kotlin.Short).toInt()) },
        { buf -> buf.readShort() }
    ),
    UnsignedShort(
        { buf, value -> buf.writeShort((value as kotlin.Short).toInt()) },
        { buf -> buf.readShort() }
    ),
    Int(
        { buf, value -> buf.writeInt(value as kotlin.Int) },
        { buf -> buf.readInt() }
    ),
    Long(
        { buf, value -> buf.writeLong(value as kotlin.Long) },
        { buf -> buf.readLong() }
    ),
    Float(
        { buf, value -> buf.writeFloat(value as kotlin.Float) },
        { buf -> buf.readFloat() }
    ),
    Double(
        { buf, value -> buf.writeDouble(value as kotlin.Double) },
        { buf -> buf.readDouble() }
    ),
    String(
        { buf, value -> buf.writeUTF(value as kotlin.String) },
        { buf -> buf.readUTF() }
    ),
    Chat(
        { buf, value -> TODO() },
        { buf -> TODO() }
    ),
    Identifier(
        { buf, value -> buf.writeUTF((value as org.jglrxavpok.mcclient.Identifier).toString()) },
        { buf -> org.jglrxavpok.mcclient.Identifier(buf.readUTF()) }
    ),
    VarInt(
        { buf, value -> buf.writeVarInt(value as kotlin.Int) },
        { buf -> buf.readVarInt() }
    ),
    VarLong(
        { buf, value -> buf.writeVarLong(value as kotlin.Long) },
        { buf -> buf.readVarLong() }
    ),
    EntityMetadata(
        { buf, value -> TODO() },
        { buf -> TODO() }
    ),
    Slot(
        { buf, value -> TODO() },
        { buf -> TODO() }
    ),
    NBTTag(
        { buf, value ->
            NBTWriter(ByteBufOutputStream(buf), compressed = false).use {
                it.writeNamed("", value as NBTCompound)
            }
        },
        { buf ->
            NBTReader(ByteBufInputStream(buf), compressed = false).use {
                it.readNamed().second
            }
        }
    ),
    Position(
        { buf, value -> TODO() },
        { buf -> TODO() }
    ),
    Angle(
        { buf, value -> TODO() },
        { buf -> TODO() }
    ),
    UUID(
        { buf, value -> (value as java.util.UUID); buf.writeLong(value.mostSignificantBits); buf.writeLong(value.leastSignificantBits) },
        { buf ->
            val msb = buf.readLong()
            val lsb = buf.readLong()
            java.util.UUID(msb, lsb)
        }
    ),
    Optional(
            // has special handling
        { buf, value ->  },
        { buf -> null }
    ),
    Array(
            // has special handling
        { buf, value ->  },
        { buf -> null }
    ),
    Enum(
        { buf, value -> TODO() },
        { buf -> TODO() }
    ),
    ByteArray(
        { buf, value ->
            buf.writeBytes(value as kotlin.ByteArray)
        },
        { buf ->
            val length = buf.writerIndex()-buf.readerIndex()
            val array = kotlin.ByteArray(length)
            buf.readBytes(array)
            array
        }
    ),

    Recipe(
        { buf, value -> TODO() },
        { buf -> TODO() }
    ),

    Tag(
        { buf, value -> value as org.jglrxavpok.mcclient.game.NetworkTag; value.write(buf) },
        { buf -> org.jglrxavpok.mcclient.game.NetworkTag.read(buf) }
    )
    ;

    fun write(buffer: ByteBuf, value: Any?) {
        writingFunction(buffer, value)
    }

    fun read(buffer: ByteBuf): Any? = readingFunction(buffer)
}