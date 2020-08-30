package org.jglrxavpok.mcclient.network

import org.jglrxavpok.mcclient.network.data.DataType

/**
 * Allows automatic (de-)serialization of packets
 */
annotation class Serializable(
        /**
         * Used to sort properties to ensure correct packet format
         */
        val ordinal: Int,

        /**
         * Type of data to store
         */
        val type: DataType,

        /**
         * Should convert to enum type?
         *
         * For type = Array|Optional, convert element(s)
         */
        val isEnum: Boolean = false,

        /**
         * Used by Optional, Array to determine the type of the encoded element
         */
        val subType: DataType = DataType.Boolean
)