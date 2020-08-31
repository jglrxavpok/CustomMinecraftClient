package org.jglrxavpok.mcclient.network.play.clientbound

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.network.NetworkSettings
import org.jglrxavpok.mcclient.network.Packet
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.ServerPacket
import org.jglrxavpok.mcclient.network.data.DataType

class UnlockRecipes: ServerPacket {

    enum class Action {
        Init, Add, Remove
    }

    @Serializable(0, DataType.VarInt, isEnum = true) lateinit var action: Action
    @Serializable(1, DataType.Boolean) var craftingRecipeBookOpen = false
    @Serializable(2, DataType.Boolean) var craftingRecipeBookFilterActive = false
    @Serializable(3, DataType.Boolean) var smeltingRecipeBookOpen = false
    @Serializable(4, DataType.Boolean) var smeltingRecipeBookFilterActive = false
    @Serializable(5, DataType.Boolean) var blastFurnaceRecipeBookOpen = false
    @Serializable(6, DataType.Boolean) var blastFurnaceRecipeBookFilterActive = false
    @Serializable(7, DataType.Boolean) var smokerRecipeBookOpen = false
    @Serializable(8, DataType.Boolean) var smokerRecipeBookFilterActive = false
    @Serializable(9, DataType.Array, subType = DataType.Identifier) lateinit var recipeIDs: Array<Identifier>
    var recipeIDToAddToBook: Array<Identifier> = emptyArray()

    override fun handle(networkSettings: NetworkSettings, ctx: ChannelHandlerContext) {
        println("unlock recipes")
    }

    override fun readFrom(buffer: ByteBuf): Packet {
        super.readFrom(buffer)
        if(action == Action.Init) {
            val toAddCount = DataType.VarInt.read(buffer) as Int
            recipeIDToAddToBook = Array(toAddCount) { Identifier.None }
            for(i in recipeIDToAddToBook.indices) {
                recipeIDToAddToBook[i] = DataType.Identifier.read(buffer) as Identifier
            }
        }
        return this
    }

    override fun writeTo(buffer: ByteBuf) {
        super.writeTo(buffer)
        if(action == Action.Init) {
            DataType.VarInt.write(buffer, recipeIDToAddToBook.size)
            for(id in recipeIDToAddToBook) {
                DataType.Identifier.write(buffer, id)
            }
        }
    }
}