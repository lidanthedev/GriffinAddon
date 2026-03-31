package me.lidan.griffinAddon.abilities;

import com.google.gson.JsonObject;
import me.lidan.cavecrawlers.items.abilities.ClickAbility;
import me.lidan.cavecrawlers.items.abilities.ItemAbility;
import me.lidan.cavecrawlers.stats.Stat;
import me.lidan.cavecrawlers.stats.Stats;
import me.lidan.cavecrawlers.stats.StatsCalculateEvent;
import me.lidan.cavecrawlers.utils.BukkitUtils;
import me.lidan.cavecrawlers.utils.Cooldown;
import me.lidan.griffinAddon.GriffinAddon;
import me.lidan.griffinAddon.griffin.GriffinManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class SpadeAbility extends ClickAbility {
    private final GriffinManager griffinManager = GriffinManager.getInstance();
    private final Cooldown<UUID> chargeCooldown = new Cooldown<>();
    private int activeTime = 10000;
    private int amount = 10;

    public SpadeAbility() {
        super("Spade", "Increase your chance to find treasure", 20, 50000);
    }

    @Override
    public String getDescription() {
        return "Increase your chance to find treasure by " + ChatColor.GREEN + amount + "%" + ChatColor.GRAY + " for " + ChatColor.GREEN + activeTime / 1000 + " seconds";
    }

    @Override
    protected boolean useAbility(PlayerEvent playerEvent) {
        Player player = playerEvent.getPlayer();
        player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 100);
        chargeCooldown.setCooldown(player.getUniqueId(), System.currentTimeMillis());
        player.playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 1, 2);
        return true;
    }

    @Override
    public ItemAbility buildAbilityWithSettings(JsonObject map) {
        SpadeAbility ability = (SpadeAbility) super.buildAbilityWithSettings(map);
        if (map.has("amount")) {
            ability.amount = map.get("amount").getAsInt();
        }
        if (map.has("activeTime")) {
            ability.activeTime = map.get("activeTime").getAsInt();
        }
        return ability;
    }

    @EventHandler
    public void onStatsUpdate(StatsCalculateEvent event){
        Player player = event.getPlayer();
        Stats stats = event.getStats();
        if (System.currentTimeMillis() - chargeCooldown.getCooldown(player.getUniqueId()) >= activeTime) {
            return;
        }
        Stat stat = stats.get(GriffinAddon.GRIFFIN_LUCK);
        stat.add(amount);
    }
}
