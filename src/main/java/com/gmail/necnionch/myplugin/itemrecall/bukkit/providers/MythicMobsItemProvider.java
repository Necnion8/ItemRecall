package com.gmail.necnionch.myplugin.itemrecall.bukkit.providers;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.ItemRecallPlugin;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.Item;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemProvider;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemResolver;
import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.items.ItemManager;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class MythicMobsItemProvider implements ItemProvider {

    private static final Logger log = ItemRecallPlugin.getPlugin(ItemRecallPlugin.class).getLogger();
    private final ItemManager itemManager;

    public MythicMobsItemProvider(MythicPlugin mythicPlugin) {
        itemManager = mythicPlugin.getItemManager();
    }

    public static @Nullable MythicMobsItemProvider tryHook() {
        PluginManager pm = Bukkit.getPluginManager();

        if (pm.isPluginEnabled("MythicMobs")) {
            try {
                return new MythicMobsItemProvider(MythicProvider.get());

            } catch (IllegalStateException e) {
                log.warning("Unable to hook to MythicMobs: " + e.getMessage());
            }
        }
        return null;
    }



    @Nullable
    @Override
    public ItemResolver create(Item item) {
        return new Resolver(item.getName());
    }


    public class Resolver implements ItemResolver {

        private final String item;

        public Resolver(String item) {
            this.item = item;
        }

        @Override
        public boolean matchItem(@NotNull ItemStack itemStack) {
            return item.equals(itemManager.getMythicTypeFromItem(itemStack));
        }

        @Override
        public ItemStack createItem(@Nullable Player player) throws UnavailableError {
            return itemManager.getItem(item)
                    .map(mItem -> mItem.generateItemStack(1))
                    .map(BukkitAdapter::adapt)
                    .orElseThrow(UnavailableError::new);
        }
    }

}
