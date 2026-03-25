package me.lidan.griffinAddon.griffin;

import me.lidan.cavecrawlers.utils.MiniMessageUtils;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.concurrent.TimeUnit;

@Command("griffin")
@CommandPermission("griffin.command")
public class GriffinCommand {
    private final GriffinManager griffinManager = GriffinManager.getInstance();

    @Subcommand("lava")
    @CommandPermission("griffin.command.lava")
    @Cooldown(value = 1, unit = TimeUnit.SECONDS)
    public void lava(Player sender, @Default("1") float pitch){
        sender.stopSound(Sound.MUSIC_DISC_LAVA_CHICKEN);
        sender.playSound(sender, Sound.MUSIC_DISC_LAVA_CHICKEN, SoundCategory.RECORDS, 1, pitch);
        sender.sendMessage(MiniMessageUtils.miniMessage("<red>La la la Lava..."));
    }
}
