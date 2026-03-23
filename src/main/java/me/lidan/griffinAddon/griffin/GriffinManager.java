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
    private static GriffinManager instance;
    private HashMap<UUID, Block> griffinMap = new HashMap<>();
    private HashMap<UUID, Rarity> rarityMap = new HashMap<>();
    private HashMap<UUID, GriffinProtection> griffinProtectionMap = new HashMap<>();
    private World world;
    private final Map<UUID, Map<Location,GriffinBrokenBlockInfo>> brokenBlocks = new HashMap<>();
    private final BiMap<Rarity, Material> rarityToBlockMap = ImmutableBiMap.<Rarity, Material>builder()
            .put(Rarity.COMMON, Material.WHITE_STAINED_GLASS)
            .put(Rarity.UNCOMMON, Material.GREEN_STAINED_GLASS)
            .put(Rarity.RARE, Material.BLUE_STAINED_GLASS)
            .put(Rarity.EPIC, Material.PURPLE_STAINED_GLASS)
            .put(Rarity.LEGENDARY, Material.ORANGE_STAINED_GLASS)
            .put(Rarity.MYTHIC, Material.PINK_STAINED_GLASS)
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

    public void handleGriffinBreak(Player player, Block block){
        griffinMap.remove(player.getUniqueId());
        ItemInfo itemInfo = ItemsManager.getInstance().getItemFromItemStackSafe(player.getInventory().getItemInMainHand());
        if (itemInfo == null){
            return;
        }
        if (!(itemInfo.getAbility() instanceof SpadeAbility)){
            return;
        }
        Rarity rarity = itemInfo.getRarity();
        Location loc = block.getLocation().add(0,2,0);


        if (rarity == null) return;

        grffinDropsMap.get(rarity).drop(player, loc);
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
        return name.contains("[Level ") && name.contains("]");
    }

    public int getGriffinMobLevel(String name) {
        // griffin mob name appear in this format: [Level 1] Minos Hunter
        name = ChatColor.stripColor(name);
        String[] split = name.split(" ");
        if (split.length < 2){
            return 0;
        }
        String level = split[1].split("]")[0];
        return Integer.parseInt(level);
    }

    public Map<Location, GriffinBrokenBlockInfo> getBrokenBlocksMapOfPlayer(Player player) {
        return brokenBlocks.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
    }

    public void handleBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block.getType() != Material.SAND && block.getType() != Material.RED_SAND) return;
        ItemInfo itemInfo = ItemsManager.getInstance().getItemFromItemStack(player.getInventory().getItemInMainHand());
        if (itemInfo == null) return;
        Map<Location, GriffinBrokenBlockInfo> brokenBlocksMapOfPlayer = getBrokenBlocksMapOfPlayer(player);
        GriffinBrokenBlockInfo brokenBlockInfo = brokenBlocksMapOfPlayer.get(block.getLocation());
        if (brokenBlocksMapOfPlayer.containsKey(block.getLocation()) && System.currentTimeMillis() - brokenBlockInfo.time() < BLOCK_BREAK_COOLDOWN){
            changeBlock(player, block, brokenBlockInfo.blockData());
            return;
        }
        brokenBlocksMapOfPlayer.put(block.getLocation(), new GriffinBrokenBlockInfo(player, block.getBlockData(), System.currentTimeMillis()));
        if (RandomUtils.chanceOf(30)){
            changeBlock(player, block, PURPLE_STAINED_GLASS_BLOCK_DATA);
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
}
