package org.jglrxavpok.mcclient.rendering.models

import org.jglrxavpok.mcclient.Identifier

object JsonModelLoader {

    private val backingMap = mutableMapOf<Identifier, JsonModel>()

    fun getOrLoad(id: Identifier): JsonModel {
        // TODO: intercept cyclic dependencies
        if(!backingMap.containsKey(id)) {
            backingMap[id] = JsonModel(id)
        }
        return backingMap[id]!!
    }
}