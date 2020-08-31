package org.jglrxavpok.mcclient

class Identifier(val domain: String, val path: String) {

    companion object {
        val None = Identifier("none:none")
    }
    // TODO: validation

    constructor(id: String): this(
            if(':' in id) id.substringBefore(':') else "minecraft",
            if(':' in id) id.substringAfter(':') else id
    )

    override fun toString(): String {
        return "$domain:$path"
    }
}