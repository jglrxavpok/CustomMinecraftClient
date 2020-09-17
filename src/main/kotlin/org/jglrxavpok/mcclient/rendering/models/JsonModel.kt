package org.jglrxavpok.mcclient.rendering.models

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.jglrxavpok.mcclient.IO
import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.Direction
import org.jglrxavpok.mcclient.game.blocks.BlockState
import org.jglrxavpok.mcclient.rendering.MeshBuilder

class JsonModel(id: Identifier): MinecraftModel {

    companion object {
        private val gson = Gson()
    }

    private val textureMap = hashMapOf<String, String>()
    private val faces = mutableListOf<Face>()

    init {
        val fullID = id.prependPath("models").withExtension("json")
        val obj = IO.openStream(fullID).bufferedReader().use {
            gson.fromJson(it, JsonObject::class.java)
        }
        if(obj.has("parent")) {
            copyParent(Identifier(obj.get("parent").asString))
        }

        if(obj.has("elements")) {
            for(elem in obj.getAsJsonArray("elements")) {
                loadElement(elem.asJsonObject)
            }
        }

        if(obj.has("textures")) {
            for((textureID, texturePath) in obj.getAsJsonObject("textures").entrySet()) {
                textureMap["#$textureID"] = texturePath.asString
            }
        }
    }

    private fun copyParent(parentPath: Identifier) {
        val parent = JsonModelLoader.getOrLoad(parentPath)
        faces += parent.faces
        textureMap += parent.textureMap
    }

    private fun loadElement(obj: JsonObject) {
        val from = obj.getAsJsonArray("from").map { it.asDouble }
        val to = obj.getAsJsonArray("to").map { it.asDouble }

        val minX = from[0].toFloat()
        val minY = from[1].toFloat()
        val minZ = from[2].toFloat()
        val maxX = to[0].toFloat()
        val maxY = to[1].toFloat()
        val maxZ = to[2].toFloat()
        val faceDescriptions = obj.getAsJsonObject("faces")
        for(faceName in Direction.values()) {
            val face = faceDescriptions.getAsJsonObject(faceName.name.toLowerCase()) ?: continue
            // TODO: cullface
            val texture = face.get("texture")?.asString ?: "#none"
            val compiledFace = when(faceName) {
                Direction.North -> {
                    Face(texture, minX, minY, minZ, maxX, maxY, minZ)
                }
                Direction.South -> {
                    Face(texture, minX, minY, maxZ, maxX, maxY, maxZ)
                }
                Direction.East -> {
                    Face(texture, minX, minY, minZ, minX, maxY, maxZ)
                }
                Direction.West -> {
                    Face(texture, maxX, minY, minZ, maxX, maxY, maxZ)
                }
                Direction.Up -> {
                    Face(texture, minX, maxY, minZ, maxX, maxY, maxZ)
                }
                Direction.Down -> {
                    Face(texture, minX, minY, minZ, maxX, minY, maxZ)
                }
            }
            faces += compiledFace
        }
    }

    private fun resolveTexture(id: String): Identifier {
        if(id.startsWith("#"))
            return resolveTexture(id.substring(1))
        return Identifier(textureMap.getOrDefault(id, "minecraft:missigno"))
    }

    override fun fillQuads(meshBuilder: MeshBuilder, state: BlockState, x: Int, y: Int, z: Int) {
        for(face in faces) {
            face.fillQuad(meshBuilder)
        }
    }

    override fun textures(): List<Identifier> {
        return textureMap
                .filter { !it.key.startsWith("#") }
                .map { Identifier(it.value) }
    }

    private inner class Face(val texture: String, val minX: Float, val minY: Float, val minZ: Float, val maxX: Float, val maxY: Float, val maxZ: Float) {

        private val resolvedTexture get()= resolveTexture(texture)

        fun fillQuad(meshBuilder: MeshBuilder) {
            // TODO: load from atlas
            val minU = 0f
            val maxU = 0f
            val minV = 1f
            val maxV = 1f
            meshBuilder
                    .vertex(minX, minY, minZ, minU, minV)
                    .vertex(minX, maxY, minZ, minU, maxV)
                    .vertex(maxX, maxY, maxZ, maxU, maxV)
                    .vertex(maxX, minY, maxZ, maxU, minV)
        }
    }
}