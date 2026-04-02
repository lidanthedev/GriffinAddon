package me.lidan.griffinAddon.griffin;

import fr.skytasul.glowingentities.GlowingEntities;
import me.lidan.cavecrawlers.utils.MiniMessageUtils;
import me.lidan.griffinAddon.GriffinAddon;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.EntitySelector;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.concurrent.TimeUnit;

@Command("griffin")
@CommandPermission("griffin.command")
public class GriffinCommand {
    private final GriffinManager griffinManager = GriffinManager.getInstance();
    private final GlowingEntities glowingEntities = GriffinAddon.getInstance().getGlowingEntities();

    @Subcommand("lava")
    @CommandPermission("griffin.command.lava")
    @Cooldown(value = 1, unit = TimeUnit.SECONDS)
    public void lava(Player sender, @Default("1") float pitch) {
        sender.stopSound(Sound.MUSIC_DISC_LAVA_CHICKEN);
        sender.playSound(sender, Sound.MUSIC_DISC_LAVA_CHICKEN, SoundCategory.RECORDS, 1, pitch);
        sender.sendMessage(MiniMessageUtils.miniMessage("<red>La la la Lava..."));
    }

    @Subcommand("glow")
    @CommandPermission("griffin.command.glow")
    public void glow(CommandSender sender, EntitySelector<Entity> entitySelector, @Default("WHITE") ChatColor color) {
        for (Entity entity : entitySelector) {
            if (!entity.isDead()) {
                for (Player player : entity.getWorld().getPlayers()) {
                    try {
                        glowingEntities.setGlowing(entity, player, color);
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
