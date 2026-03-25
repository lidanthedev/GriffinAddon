package me.lidan.griffinAddon.listeners;

import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import me.lidan.cavecrawlers.objects.ConfigMessage;
import me.lidan.griffinAddon.griffin.GriffinManager;
import me.lidan.griffinAddon.griffin.GriffinProtection;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class GriffinListener implements Listener {
    private static final Logger log = LoggerFactory.getLogger(GriffinListener.class);
    private final ConfigMessage GRIFFIN_PROTECTED = ConfigMessage.getMessageOrDefault("griffin_protected", "Mob Protected for %time%");
    private final ConfigMessage GRIFFIN_UNDER_LEVELED = ConfigMessage.getMessageOrDefault("griffin_under_leveled", "This mob is level %griffin_level%. You are level %player_griffin_level%. Use your spade to update your level!");
    private final GriffinManager griffinManager = GriffinManager.getInstance();

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        HashMap<UUID, GriffinProtection> griffinProtectionMap = griffinManager.getGriffinProtectionMap();
        griffinProtectionMap.remove(event.getEntity().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMythicMobDespawn(MythicMobDespawnEvent event) {
        Entity entity = event.getEntity();
        if (griffinManager.isGriffinMob(entity)) {
            griffinManager.getGriffinProtectionMap().remove(entity.getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (player.getWorld() != griffinManager.getWorld()) return;
        event.setCancelled(true);
        griffinManager.handleBlockBreak(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamagedByBlock(EntityDamageByBlockEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (player.getWorld() != griffinManager.getWorld()) return;
        event.setCancelled(true);
    }
}
