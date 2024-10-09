package com.gmail.necnionch.myplugin.itemrecall.bukkit;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.event.ItemRecallEvent;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.Item;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemProvider;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemResolver;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ReplaceItem;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.providers.MythicMobsItemProvider;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

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

        // PluginManagerによるロードを考慮する
        getServer().getOnlinePlayers().forEach(p ->
                replaceInventoryAll(p.getInventory(), p, null));
    }


    public void debugPrint(String m) {
        if (config.isEnableDebug())
            getLogger().warning("[DEBUG]: " + m);
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

    public Optional<Supplier<ItemStack>> createReplacer(@Nullable Player player, ItemStack itemStack, @Nullable Event event) {
        ReplaceItem replaceItem = getReplaceItemOfItemStack(itemStack);
        if (replaceItem == null)
            return Optional.empty();
        return createReplacer(replaceItem, player, itemStack, event);
    }

    public Optional<Supplier<ItemStack>> createReplacer(@NotNull ReplaceItem replaceItem, @Nullable Player player, ItemStack itemStack, @Nullable Event event) {
        Item newItem = replaceItem.getNewItem();
        if (newItem == null) {
            // remove only
            ItemRecallEvent newEvent = new ItemRecallEvent(player, replaceItem, itemStack, null, event);
            getServer().getPluginManager().callEvent(newEvent);

            return newEvent.isCancelled() ? Optional.empty() : Optional.of(newEvent::getNewItemStack);
        }

        ItemResolver newItemResolver = newItem.getResolver();
        if (newItemResolver == null) {
            return Optional.empty();  // newItem作成できない場合は、oldItemを削除せず何もしない
        }

        // replace new item
        ItemStack newItemStack = null;
        boolean ignore = false;
        try {
            newItemStack = newItemResolver.createItem(player, itemStack.getAmount());
        } catch (ItemResolver.UnavailableError e) {
            getLogger().severe("Failed to generate item: (t:" + newItem.getType() + ",i:" + newItem.getName() + "): " + e.getMessage());
            ignore = true;
        }
        ItemRecallEvent newEvent = new ItemRecallEvent(player, replaceItem, itemStack, newItemStack, event);
        getServer().getPluginManager().callEvent(newEvent);

        if (newEvent.isChangedNewItemStack()) {
            ignore = false;
        }

        return (newEvent.isCancelled() || ignore) ? Optional.empty() : Optional.of(newEvent::getNewItemStack);
    }

    public void replaceInventoryAll(Inventory inventory, @Nullable Player owner, @Nullable Event event) {
        debugPrint("replaceInventoryAll -> " + (owner != null ? owner.getName() : inventory));
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            if (itemStack == null)
                continue;

            ReplaceItem replaceItem = getReplaceItemOfItemStack(itemStack);
            if (replaceItem == null)
                continue;
            debugPrint("== inventory slot: " + i + " ==");
            debugPrint("  OLD -> " + itemStack);

            Supplier<ItemStack> replacer = createReplacer(replaceItem, owner, itemStack, event).orElse(null);
            if (replacer == null) {
                debugPrint("  NEW -> NO_REPLACER");
            } else {
                ItemStack newItemStack = replacer.get();
                debugPrint("  NEW -> " + newItemStack);
                inventory.setItem(i, newItemStack);
            }
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        replaceInventoryAll(player.getInventory(), player, event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        org.bukkit.entity.Item item = event.getItem();
        ItemStack itemStack = item.getItemStack();

        createReplacer(player, itemStack, event).ifPresent(r -> {
            ItemStack newItemStack = r.get();
            if (newItemStack != null) {
                item.setItemStack(newItemStack);
            } else {
                event.setCancelled(true);
                item.remove();
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onSelect(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        ItemStack itemStack = inv.getItem(event.getNewSlot());

        createReplacer(player, itemStack, event).ifPresent(r -> inv.setItem(event.getNewSlot(), r.get()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        org.bukkit.entity.Item item = event.getItemDrop();

        createReplacer(player, item.getItemStack(), event).ifPresent(r -> {
            ItemStack newItemStack = r.get();
            if (newItemStack != null) {
                item.setItemStack(newItemStack);
            } else {
                event.setCancelled(true);
                item.remove();
            }
        });
    }

    private final Set<Player> playersCloseToScan = Sets.newHashSet();

    @EventHandler
    public void onClickInv(InventoryClickEvent event) {
        if (InventoryAction.NOTHING.equals(event.getAction()))
            return;
        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();
        if (playersCloseToScan.contains(player))
            return;

        ItemStack itemStack = event.getCursor();
        if (itemStack != null && getReplaceItemOfItemStack(itemStack) != null) {
            playersCloseToScan.add(player);
            debugPrint("scan marked to " + player.getName());
        }
    }

    @EventHandler
    public void onCloseInv(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (playersCloseToScan.remove(player)) {
            replaceInventoryAll(player.getInventory(), player, event);
        }
    }

}
