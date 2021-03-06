package org.jglrxavpok.mcclient.rendering.models

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.jglrxavpok.mcclient.IO
import org.jglrxavpok.mcclient.Identifier
import org.jglrxavpok.mcclient.game.Direction
import org.jglrxavpok.mcclient.game.blocks.BlockState
import org.jglrxavpok.mcclient.rendering.MeshBuilder
import org.jglrxavpok.mcclient.rendering.WorldRenderer
import org.joml.Matrix4fStack
import org.joml.Vector3f

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
        faces += parent.faces.map { Face(it.texture, it.hasTint, it.minX, it.minY, it.minZ, it.maxX, it.maxY, it.maxZ) }
        textureMap += parent.textureMap
    }

    private fun loadElement(obj: JsonObject) {
        val from = obj.getAsJsonArray("from").map { it.asDouble }
        val to = obj.getAsJsonArray("to").map { it.asDouble }

        val minX = from[0].toFloat()/16f
        val minY = from[1].toFloat()/16f
        val minZ = from[2].toFloat()/16f
        val maxX = to[0].toFloat()/16f
        val maxY = to[1].toFloat()/16f
        val maxZ = to[2].toFloat()/16f
        val faceDescriptions = obj.getAsJsonObject("faces")
        for(faceName in Direction.values()) {
            val face = faceDescriptions.getAsJsonObject(faceName.name.toLowerCase()) ?: continue
            // TODO: cullface
            val hasTint = face.has("tintindex")
            val texture = face.get("texture")?.asString ?: "#none"
            val compiledFace = when(faceName) {
                Direction.North -> {
                    Face(texture, hasTint, minX, maxY, minZ, maxX, minY, minZ)
                }
                Direction.South -> {
                    Face(texture, hasTint, minX, maxY, maxZ, maxX, minY, maxZ)
                }
                Direction.East -> {
                    Face(texture, hasTint, minX, maxY, minZ, minX, minY, maxZ)
                }
                Direction.West -> {
                    Face(texture, hasTint, maxX, maxY, minZ, maxX, minY, maxZ)
                }
                Direction.Up -> {
                    Face(texture, hasTint, minX, maxY, minZ, maxX, maxY, maxZ)
                }
                Direction.Down -> {
                    Face(texture, hasTint, minX, minY, minZ, maxX, minY, maxZ)
                }
            }
            faces += compiledFace
        }
    }

    private fun resolveTexture(id: String): Identifier {
        val value = textureMap.getOrDefault(id, "minecraft:missingno")
        if(value.startsWith("#")) {
            return resolveTexture(value)
        }
        return Identifier(value);
    }

    override fun fillQuads(matrixStack: Matrix4fStack, meshBuilder: MeshBuilder, state: BlockState, x: Int, y: Int, z: Int, tintColor: Vector3f) {
        for(face in faces) {
            face.fillQuad(matrixStack, meshBuilder, tintColor)
        }
    }

    override fun textures(): List<Identifier> {
        return textureMap
                .filter { !it.value.startsWith("#") }
                .map { Identifier(it.value) }
    }

    private inner class Face(val texture: String, val hasTint: Boolean, val minX: Float, val minY: Float, val minZ: Float, val maxX: Float, val maxY: Float, val maxZ: Float) {

        private val resolvedTexture by lazy { resolveTexture(texture) }

        private val horizontal get() = minY==maxY

        fun fillQuad(matrixStack: Matrix4fStack, meshBuilder: MeshBuilder, tintColor: Vector3f) {
            val sprite = WorldRenderer.chunkRenderer.blockAtlas.getSprite(resolvedTexture)
            val minU = sprite.minU
            val maxU = sprite.maxU
            val minV = sprite.minV
            val maxV = sprite.maxV
            val tint by lazy { Vector3f(1f) }
            tint.set(1f)
            if(hasTint)
                tint.set(tintColor)

            val r = tintColor.x
            val g = tintColor.y
            val b = tintColor.z

            if(horizontal) {
                meshBuilder
                        .vertex(matrixStack, minX, minY, minZ, minU, minV, r, g, b)
                        .vertex(matrixStack, minX, minY, maxZ, minU, maxV, r, g, b)
                        .vertex(matrixStack, maxX, minY, maxZ, maxU, maxV, r, g, b)
                        .vertex(matrixStack, maxX, minY, minZ, maxU, minV, r, g, b)
            } else {
                meshBuilder
                        .vertex(matrixStack, minX, minY, minZ, minU, minV, r, g, b)
                        .vertex(matrixStack, minX, maxY, minZ, minU, maxV, r, g, b)
                        .vertex(matrixStack, maxX, maxY, maxZ, maxU, maxV, r, g, b)
                        .vertex(matrixStack, maxX, minY, maxZ, maxU, minV, r, g, b)
            }
        }
    }
}