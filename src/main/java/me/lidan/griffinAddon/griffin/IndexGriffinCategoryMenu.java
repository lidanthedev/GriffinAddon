package me.lidan.griffinAddon.griffin;

import me.lidan.cavecrawlers.gui.builder.item.ItemBuilder;
import me.lidan.cavecrawlers.index.IndexBaseCategoryMenu;
import me.lidan.cavecrawlers.index.IndexBlocksCategoryMenu;
import me.lidan.cavecrawlers.items.Rarity;
import me.lidan.griffinAddon.GriffinAddon;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class IndexGriffinCategoryMenu extends IndexBaseCategoryMenu {
    public IndexGriffinCategoryMenu(Player player, @NonNull String query) {
        super(player, GriffinAddon.GRIFFIN_INDEX_CATEGORY, query);
    }

    @Override
    public void setupGui() {
        Map<Rarity, GriffinDrops> dropsMap = GriffinManager.grffinDropsMap;
        List<Map.Entry<Rarity, GriffinDrops>> sortedDrops = dropsMap.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getLevel()))
                .toList();
        for (Map.Entry<Rarity, GriffinDrops> dropsEntry : sortedDrops) {
            String name = String.valueOf(dropsEntry.getKey());
            if (!ChatColor.stripColor(name.toLowerCase()).contains(query)) continue;
            addItem(name, ItemBuilder.from(griffinDropsToItemStack(dropsEntry.getKey(), dropsEntry.getValue())).asGuiItem());
        }
    }

    @Override
    public void search(String query, boolean force) {
        if (this.query.equals(query) && !force) {
            // Same query, do nothing
            return;
        }
        new IndexBlocksCategoryMenu(player, query).open();
    }

    public ItemStack griffinDropsToItemStack(Rarity rarity, GriffinDrops griffinDrops) {
        List<Component> lore = itemGenerator.dropsToLore(griffinDrops.getDrops());
        return ItemBuilder.from(GriffinManager.RARITY_TO_BLOCK_MAP.get(rarity).getMaterial()).setName(rarity.toString()).lore(lore).build();
    }
}
