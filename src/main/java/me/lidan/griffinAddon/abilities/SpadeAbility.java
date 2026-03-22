package me.lidan.griffinAddon.abilities;

import com.google.gson.JsonObject;
import me.lidan.cavecrawlers.items.abilities.ClickAbility;
import me.lidan.cavecrawlers.items.abilities.ItemAbility;
import me.lidan.cavecrawlers.utils.BukkitUtils;
import me.lidan.griffinAddon.griffin.GriffinManager;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpadeAbility extends ClickAbility {
    private final GriffinManager griffinManager = GriffinManager.getInstance();
    private int range = 100;

    public SpadeAbility() {
        super("Spade", "Make a line of flames to a treasure", 20, 500);
    }

    @Override
    protected boolean useAbility(PlayerEvent playerEvent) {
        return true;
    }

    @Override
    public ItemAbility buildAbilityWithSettings(JsonObject map) {
        SpadeAbility ability = (SpadeAbility) super.buildAbilityWithSettings(map);
        if (map.has("range")) {
            ability.range = map.get("range").getAsInt();
        }
        return ability;
    }
}
