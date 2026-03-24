package me.lidan.griffinAddon.griffin;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.lidan.cavecrawlers.integration.MythicMobsHook;
import me.lidan.cavecrawlers.items.ItemInfo;
import me.lidan.cavecrawlers.items.ItemsManager;
import me.lidan.cavecrawlers.items.Rarity;
import me.lidan.cavecrawlers.utils.MiniMessageUtils;
import me.lidan.cavecrawlers.utils.RandomUtils;
import me.lidan.griffinAddon.GriffinAddon;
import me.lidan.griffinAddon.abilities.SpadeAbility;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Data
public class GriffinManager {
    public static final Map<Rarity, GriffinDrops> grffinDropsMap = new HashMap<>();
    public static final int MAX_DISTANCE = 110;
    public static final double MAX_DISTANCE_SQUARED = Math.pow(MAX_DISTANCE, 2);
    private static final GriffinAddon plugin = GriffinAddon.getInstance();
    public static final String WORLD_NAME = plugin.getConfig().getString("griffin.world", "griffin");
    public static final int DEFAULT_PROTECTION_TIME = 5000;
    public static final int BLOCK_BREAK_COOLDOWN = 5000;
    public static final @NotNull BlockData BLACK_WOOL_BLOCK_DATA = Material.BLACK_WOOL.createBlockData();
    public static final @NotNull BlockData PURPLE_STAINED_GLASS_BLOCK_DATA = Material.PURPLE_STAINED_GLASS.createBlockData();
    public static final int BLOCK_REMOVAL_TIMEOUT = 60000;
    private static GriffinManager instance;
    private HashMap<UUID, Block> griffinMap = new HashMap<>();
    private HashMap<UUID, Rarity> rarityMap = new HashMap<>();
    private HashMap<UUID, GriffinProtection> griffinProtectionMap = new HashMap<>();
    private World world;
    private final Map<Location,GriffinBrokenBlockInfo> brokenBlocks = new HashMap<>();
    private final BiMap<Rarity, BlockData> rarityToBlockMap = ImmutableBiMap.<Rarity, BlockData>builder()
            .put(Rarity.COMMON, Material.WHITE_STAINED_GLASS.createBlockData())
            .put(Rarity.UNCOMMON, Material.GREEN_STAINED_GLASS.createBlockData())
            .put(Rarity.RARE, Material.BLUE_STAINED_GLASS.createBlockData())
            .put(Rarity.EPIC, Material.PURPLE_STAINED_GLASS.createBlockData())
            .put(Rarity.LEGENDARY, Material.ORANGE_STAINED_GLASS.createBlockData())
            .put(Rarity.MYTHIC, Material.PINK_STAINED_GLASS.createBlockData())
            .build();

    private GriffinManager() {
        world = Bukkit.getWorld(WORLD_NAME);
        if (world == null) {
            log.warn("Griffin world not found, please check your config. value: {}", WORLD_NAME);
            return;
        }
    }

    public void registerDrop(String name, GriffinDrops drops){
        grffinDropsMap.put(Rarity.valueOf(name), drops);
    }

    public boolean handleGriffinBreak(Player player, Block block){
        Location loc = block.getLocation();
        griffinMap.remove(player.getUniqueId());
        Rarity rarity = rarityToBlockMap.inverse().get(block.getBlockData());
        if (rarity == null) return false;
        Location dropLoc = block.getLocation().add(0,2,0);
        GriffinBrokenBlockInfo removedBlockInfo = brokenBlocks.get(loc);
        if (removedBlockInfo != null) {
            // if you are not the player with the data, you need to wait 1 minute before you can break the block
            if (removedBlockInfo.player().getUniqueId() != player.getUniqueId() && System.currentTimeMillis() < removedBlockInfo.time() + BLOCK_REMOVAL_TIMEOUT) {
                player.sendMessage(MiniMessageUtils.miniMessage("<red><bold>You cannot break this block right now!"));
                return false;
            }
            loc.getBlock().setBlockData(removedBlockInfo.blockData());
            brokenBlocks.remove(loc);
        }
        else {
            loc.getBlock().setType(Material.SAND);
        }

        GriffinDrops griffinDrops = grffinDropsMap.get(rarity);
        if (griffinDrops == null) return false;
        griffinDrops.drop(player, dropLoc);
        return true;
    }

    public boolean handleGriffinClick(Player player, Block block){
        Rarity rarity = rarityToBlockMap.inverse().get(block.getType());
        if (rarity == null) return false;
        player.sendMessage(MiniMessageUtils.miniMessage("<green><bold>Griffin Block Rarity: %s".formatted(rarity.name())));
        return true;
    }

    public static GriffinManager getInstance() {
        if (instance == null) {
            instance = new GriffinManager();
        }
        return instance;
    }

    public Entity spawnMob(String mob, Location location, Player player) {
        Entity entity = MythicMobsHook.getInstance().spawnMythicMob(mob, location);
        if (entity == null) return null;
        protectMobForPlayer(player, entity);
        return entity;
    }

    public void protectMobForPlayer(Player player, Entity entity) {
        if (entity instanceof LivingEntity livingEntity)
            griffinProtectionMap.put(entity.getUniqueId(), new GriffinProtection(System.currentTimeMillis(), (long) livingEntity.getHealth() + DEFAULT_PROTECTION_TIME, player.getUniqueId()));
    }

    public boolean isGriffinMob(Entity victim) {
        String name = ChatColor.stripColor(victim.getName());
        return name.contains("[Griffin ") && name.contains("]");
    }

    public int getGriffinMobLevel(String name) {
        // griffin mob name appear in this format: [Griffin 1] Minos Hunter
        name = ChatColor.stripColor(name);
        String[] split = name.split(" ");
        if (split.length < 2){
            return 0;
        }
        String level = split[1].split("]")[0];
        return Integer.parseInt(level);
    }

    public void handleBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location above = block.getLocation().add(0,1,0);
        if (above.getBlock().getType() != Material.AIR && above.getBlock().getType() != block.getType()) return;
        if (handleGriffinBreak(player, block)) return;
        if (block.getType() != Material.SAND && block.getType() != Material.RED_SAND) return;
        ItemInfo itemInfo = ItemsManager.getInstance().getItemFromItemStack(player.getInventory().getItemInMainHand());
        if (itemInfo == null) return;
        Rarity rarity = itemInfo.getRarity();
        brokenBlocks.put(block.getLocation(), new GriffinBrokenBlockInfo(player, block.getBlockData(), System.currentTimeMillis()));
        if (RandomUtils.chanceOf(1)){
            changeBlock(player, block, rarityToBlockMap.get(rarity));
        }
        else {
            changeBlock(player, block, BLACK_WOOL_BLOCK_DATA);
            changeBlock(player, block, block.getBlockData(), 20L * 5);
        }
    }

    public void changeBlock(Player player, Block block, BlockData blockData) {
        changeBlock(player, block, blockData, 1L);
    }

    public void changeBlock(Player player, Block block, BlockData blockData, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || player.getWorld() != getWorld() || !(player.getLocation().distanceSquared(block.getLocation()) < MAX_DISTANCE_SQUARED)) {
                    return;
                }
                block.setBlockData(blockData);
            }
        }.runTaskLater(GriffinAddon.getInstance(), delay);
    }

    public void cleanup(){
        for (Map.Entry<Location, GriffinBrokenBlockInfo> locationGriffinBrokenBlockInfoEntry : brokenBlocks.entrySet()) {
            Location loc = locationGriffinBrokenBlockInfoEntry.getKey();
            GriffinBrokenBlockInfo removedBlockInfo = locationGriffinBrokenBlockInfoEntry.getValue();
            if (removedBlockInfo == null) continue;
            loc.getBlock().setBlockData(removedBlockInfo.blockData());
        }
        brokenBlocks.clear();
    }
}
