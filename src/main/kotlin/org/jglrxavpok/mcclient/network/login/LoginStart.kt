package org.jglrxavpok.mcclient.network.login

import org.jglrxavpok.mcclient.network.Packet
import org.jglrxavpok.mcclient.network.Serializable
import org.jglrxavpok.mcclient.network.data.DataType

class LoginStart: Packet {
    @Serializable(0, DataType.String) var username: String = ""
}