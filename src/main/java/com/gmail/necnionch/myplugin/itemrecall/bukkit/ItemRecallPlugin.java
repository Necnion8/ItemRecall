package com.gmail.necnionch.myplugin.itemrecall.bukkit;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemProvider;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.listeners.MythicMobsListener;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ItemRecallPlugin extends JavaPlugin {
    public static final Map<String, ItemProvider> PROVIDERS = Maps.newHashMap();

    private final ItemRecallConfig config = new ItemRecallConfig(this);

    public static @Nullable ItemProvider getItemProviderOfType(String type) {
        return PROVIDERS.get(type);
    }


    @Override
    public void onEnable() {
        config.load();
        PluginManager pm = getServer().getPluginManager();

        if (pm.isPluginEnabled("MythicMobs")) {
            Object hooked = null;
            try {
                MythicPlugin mythicPlugin = MythicProvider.get();
                hooked = new MythicMobsListener(config, mythicPlugin);

            } catch (IllegalStateException e) {
                getLogger().warning("Unable to hook to MythicMobs: " + e.getMessage());

            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (hooked != null) {
                getLogger().info("Hooked MythicMobs");
            }
        }

    }

}
