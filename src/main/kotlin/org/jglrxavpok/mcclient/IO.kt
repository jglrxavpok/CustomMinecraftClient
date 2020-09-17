package org.jglrxavpok.mcclient

import java.io.File
import java.io.InputStream

object IO {

    fun openStream(id: Identifier): InputStream {
        return File("assets/${id.domain}/${id.path}").inputStream()
    }
}