package com.gmail.necnionch.myplugin.bukkit.itemrecall;

import com.gmail.necnionch.myplugin.bukkit.itemrecall.listeners.MythicMobsListener;
import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemRecallPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();

        if (pm.isPluginEnabled("MythicMobs")) {
            Object hooked = null;
            try {
                MythicPlugin mythicPlugin = MythicProvider.get();
                hooked = new MythicMobsListener(mythicPlugin);

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

    @Override
    public void onDisable() {
    }

}
