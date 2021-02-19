package com.github.monun.blocktrigger.plugin

import com.github.monun.blocktrigger.BlockTriggerManager
import com.github.monun.blocktrigger.TriggerAction
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent

class EventListener(
    private val manager: BlockTriggerManager
) : Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onBlockBreak(event: BlockBreakEvent) {
        manager.getTrigger(event.block)?.run {
            activate(TriggerAction.BREAKING)

            if (!allowBreak) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onBlockPlace(event: BlockPlaceEvent) {
        manager.getTrigger(event.block)?.run {
            activate(TriggerAction.PLACING)

            if (!allowPlace) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onEntityExplode(event: EntityExplodeEvent) {
        val iterator = event.blockList().iterator()

        while (iterator.hasNext()) {
            val block = iterator.next()
            manager.getTrigger(block)?.run {
                activate(TriggerAction.EXPLOSION)

                if (!allowBreak) {
                    iterator.remove()
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onBlockExplode(event: BlockExplodeEvent) {
        val iterator = event.blockList().iterator()

        while (iterator.hasNext()) {
            val block = iterator.next()
            manager.getTrigger(block)?.run {
                activate(TriggerAction.EXPLOSION)

                if (!allowBreak) {
                    iterator.remove()
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        event.clickedBlock?.let { block ->
            manager.getTrigger(block)?.run {
                activate(TriggerAction.INTERACTION)

                if (!allowInteract)
                    event.isCancelled = true
            }
        }
    }
}