package com.github.monun.blocktrigger

import org.bukkit.block.Block
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class BlockTriggerManager(
    private val folder: File
) {
    private val triggers = HashMap<Block, BlockTrigger>()

    init {
        folder.mkdirs()
        load()
    }

    fun registerTrigger(block: Block) {
        triggers.computeIfAbsent(block) {
            BlockTrigger(UUID.randomUUID()).apply { this.block = block }
        }
    }

    fun getTrigger(block: Block): BlockTrigger? {
        return triggers[block]
    }

    private fun load() {
        folder.listFiles { _, name -> name.endsWith(".yml") }!!.forEach { file ->
            val uniqueId = UUID.fromString(file.nameWithoutExtension)
            val config = YamlConfiguration.loadConfiguration(file)
            val blockTrigger = BlockTrigger(uniqueId).apply { load(config) }

            triggers[blockTrigger.block] = blockTrigger
        }
    }

    fun save() {
        folder.mkdirs()

        triggers.values.forEach { trigger ->
            val config = trigger.save()
            val file = File(folder, "${trigger.uniqueId}.yml")
            config.save(file)
        }
    }

}