package org.jglrxavpok.mcclient.network

import org.jglrxavpok.mcclient.network.handshake.NetworkState

data class NetworkSettings(var state: NetworkState, var compressionThreshold: Int = -1)