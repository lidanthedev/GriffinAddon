package me.lidan.griffinAddon.griffin;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public record GriffinBrokenBlockInfo(Player player, BlockData blockData, long time) {
}
