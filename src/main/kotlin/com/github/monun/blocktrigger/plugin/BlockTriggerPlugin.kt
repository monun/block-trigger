package com.github.monun.blocktrigger.plugin

import com.github.monun.blocktrigger.BlockTriggerManager
import com.github.monun.blocktrigger.TriggerAction
import com.github.monun.kommand.KommandContext
import com.github.monun.kommand.argument.KommandArgument
import com.github.monun.kommand.argument.suggestions
import com.github.monun.kommand.kommand
import com.github.monun.kommand.sendFeedback
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Monun
 */
class BlockTriggerPlugin : JavaPlugin() {
    private lateinit var manager: BlockTriggerManager

    override fun onEnable() {
        manager = BlockTriggerManager(dataFolder)

        server.pluginManager.registerEvents(EventListener(manager), this)

        setupCommands()
    }

    private fun setupCommands() = kommand {
        register("bt") {
            then("register") {
                require { this is Player }
                executes { ctxt ->
                    val sender = ctxt.sender as Player
                    val block = sender.getTargetBlock(64)

                    if (block == null) {
                        sender.sendFeedback("지정할 블록이 없습니다.")
                    } else {
                        manager.registerTrigger(block)
                        sender.sendFeedback("${block.world.name} ${block.x} ${block.y} ${block.z}")
                    }
                }
            }
        }
    }

    override fun onDisable() {
        manager.save()
    }
}