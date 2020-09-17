package org.jglrxavpok.mcclient.rendering.models

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.jglrxavpok.mcclient.IO
import org.jglrxavpok.mcclient.Identifier

class BlockStateModel(val id: Identifier) {

    companion object {
        private val gson = Gson()
    }

    private lateinit var _backingModel: MinecraftModel
    val backingModel get()= _backingModel

    init {
        val json = IO.openStream(id.withExtension("json")).bufferedReader().use {
            gson.fromJson(it, JsonObject::class.java)
        }
        if(json.has("variants")) {
            loadVariants(json.getAsJsonObject("variants"))
        } else {
            // TODO: multipart
            TODO()
        }
    }

    private fun loadVariants(json: JsonObject) {
        val variants = hashMapOf<String, MinecraftModel>()
        for((name, element) in json.entrySet()) {
            if(element.isJsonObject) {
                variants[name] = loadModel(element.asJsonObject)
            } else {
                val randomElements = mutableListOf<MinecraftModel>()
                for(model in element.asJsonArray) {
                    randomElements += loadModel(model.asJsonObject)
                }
                variants[name] = RandomModel(randomElements)
            }
        }
        _backingModel = VariantModel(variants)
    }

    private fun loadModel(modelDescription: JsonObject): MinecraftModel {
        val id = Identifier(modelDescription.get("model").asString)
        // TODO: random rotations
        return JsonModelLoader.getOrLoad(id)
    }


}