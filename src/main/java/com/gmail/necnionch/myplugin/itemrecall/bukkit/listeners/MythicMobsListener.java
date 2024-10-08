package com.gmail.necnionch.myplugin.itemrecall.bukkit.listeners;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.ItemRecallConfig;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.event.ItemRecallEvent;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemProvider;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ReplaceItem;
import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MythicMobsListener implements Listener {

    private final ItemRecallConfig config;
    private final ItemManager items;

    public MythicMobsListener(ItemRecallConfig config, MythicPlugin mythicPlugin) {
        this.config = config;
        this.items = mythicPlugin.getItemManager();
    }

    private @Nullable ReplaceItem getReplaceItemOfItemStack(ItemStack itemStack) {
        return config.getItemsOfOldType("mythicmobs").stream()
                .filter(r -> {
                    ItemProvider provider = r.getOldItem().getProvider();
                    return provider != null && provider.matchItem(itemStack);
                })
                .findFirst()
                .orElse(null);
    }


    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();

        String mythicItemType = items.getMythicTypeFromItem(itemStack);
        if (mythicItemType == null)
            return;

        ReplaceItem replaceItem = getReplaceItemOfItemStack(itemStack);
        if (replaceItem == null)
            return;

        if (replaceItem.getNewItem() == null) {
            // remove only
            ItemRecallEvent newEvent = new ItemRecallEvent(player, replaceItem, itemStack, null, event);
            Bukkit.getPluginManager().callEvent(newEvent);

            if (!newEvent.isCancelled()) {
                item.setItemStack(newEvent.getNewItemStack());
            }
            return;
        }

        ItemProvider newItemProvider = replaceItem.getNewItem().getProvider();
        if (newItemProvider == null) {
            return;  // not provided!!!
        }

        // replace new item
        ItemRecallEvent newEvent = new ItemRecallEvent(player, replaceItem, itemStack, newItemProvider.createItem(player), event);
        Bukkit.getPluginManager().callEvent(newEvent);

        if (!newEvent.isCancelled()) {
            item.setItemStack(newEvent.getNewItemStack());
        }
    }

}
