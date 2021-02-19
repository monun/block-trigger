package com.github.monun.blocktrigger

import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.configuration.file.YamlConfiguration
import java.util.*
import kotlin.collections.ArrayList

class BlockTrigger(
    val uniqueId: UUID
) {
    lateinit var block: Block
    private val triggersByAction = EnumMap<TriggerAction, ArrayList<String>>(TriggerAction::class.java)
    var allowPlace: Boolean = true
    var allowBreak: Boolean = true
    var allowInteract: Boolean = true

    fun add(action: TriggerAction, command: String) {
        triggersByAction.computeIfAbsent(action) { ArrayList() }.add(command)
    }

    fun activate(action: TriggerAction) {
        println(triggersByAction)

        triggersByAction[action]?.forEach {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), it)
        }
    }

    fun save(): YamlConfiguration {
        val config = YamlConfiguration()
        config.createSection("block").let { section ->
            section["world"] = block.world.name
            section["x"] = block.x
            section["y"] = block.y
            section["z"] = block.z
        }

        config["allow-break"] = allowBreak
        config["allow-place"] = allowPlace
        config["allow-interact"] = allowInteract

        val section = config.createSection("triggers")

        for ((action, commands) in triggersByAction) {
            section[action.name.toLowerCase()] = commands
        }

        return config
    }

    fun load(config: YamlConfiguration) {
        this.block = config.getConfigurationSection("block")!!.let { section ->
            Bukkit.getWorld(
                section.getString("world")!!
            )!!.getBlockAt(
                section.getInt("x"),
                section.getInt("y"),
                section.getInt("z")
            )
        }

        allowBreak = config.getBoolean("allow-break")
        allowPlace = config.getBoolean("allow-place")
        allowInteract = config.getBoolean("allow-interact")

        val section = config.getConfigurationSection("triggers") ?: return

        for (action in TriggerAction.values()) {
            val list = section.getStringList(action.name.toLowerCase())

            if (list.isNotEmpty()) {
                triggersByAction[action] = ArrayList(list)
            }
        }
    }
}