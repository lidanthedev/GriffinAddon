package me.lidan.griffinAddon.griffin;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Cooldown;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.concurrent.TimeUnit;

@Command("griffin")
@CommandPermission("griffin.command")
public class GriffinCommand {
    private final GriffinManager griffinManager = GriffinManager.getInstance();
}
