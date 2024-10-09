package com.gmail.necnionch.myplugin.itemrecall.bukkit;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.event.ItemRecallEvent;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.Item;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemProvider;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemResolver;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ReplaceItem;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.providers.MythicMobsItemProvider;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ItemRecallPlugin extends JavaPlugin implements Listener {
    public static final Map<String, ItemProvider> PROVIDERS = Maps.newHashMap();

    private final ItemRecallConfig config = new ItemRecallConfig(this);

    public static @Nullable ItemProvider getItemProviderOfType(String type) {
        return PROVIDERS.get(type);
    }

    public static @Nullable ItemResolver createItemResolver(Item item) {
        ItemProvider itemProvider = PROVIDERS.get(item.getType());
        if (itemProvider == null)
            return null;
        try {
            return itemProvider.create(item);
        } catch (ItemProvider.InvalidError e) {
            ItemRecallPlugin.getPlugin(ItemRecallPlugin.class).getLogger().severe(
                    "Failed to create item resolver: (t:" + item.getType() + ",i:" + item.getName() + "): " + e.getMessage());
            return null;
        }
    }


    @Override
    public void onEnable() {
        config.load();
        PluginManager pm = getServer().getPluginManager();

        ItemProvider provider = null;
        try {
            provider = MythicMobsItemProvider.tryHook();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (provider != null) {
            PROVIDERS.put("mythicmobs", provider);
            getLogger().info("Hooked to MythicMobs");
        }

        pm.registerEvents(this, this);
    }


    public @Nullable ReplaceItem getReplaceItemOfItemStack(ItemStack itemStack) {
        for (ReplaceItem replaceItem : config.getItems()) {
            ItemResolver resolver = replaceItem.getOldItem().getResolver();
            if (resolver == null)
                continue;

            if (resolver.matchItem(itemStack)) {
                return replaceItem;
            }
        }
        return null;
    }


    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        org.bukkit.entity.Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();

        ReplaceItem replaceItem = getReplaceItemOfItemStack(itemStack);
        if (replaceItem == null)
            return;

        Item newItem = replaceItem.getNewItem();
        if (newItem == null) {
            // remove only
            ItemRecallEvent newEvent = new ItemRecallEvent(player, replaceItem, itemStack, null, event);
            Bukkit.getPluginManager().callEvent(newEvent);

            if (!newEvent.isCancelled()) {
                item.setItemStack(newEvent.getNewItemStack());
            }
            return;
        }

        ItemResolver newItemResolver = newItem.getResolver();
        if (newItemResolver == null) {
            return;  // not provided!!!
        }

        // replace new item
        ItemStack newItemStack = null;
        try {
            newItemStack = newItemResolver.createItem(player);
        } catch (ItemResolver.UnavailableError e) {
            getLogger().severe("Failed to generate item: (t:" + newItem.getType() + ",i:" + newItem.getName() + "): " + e.getMessage());
        }
        ItemRecallEvent newEvent = new ItemRecallEvent(player, replaceItem, itemStack, newItemStack, event);
        Bukkit.getPluginManager().callEvent(newEvent);

        if (!newEvent.isCancelled()) {
            item.setItemStack(newEvent.getNewItemStack());
        }
    }

}
