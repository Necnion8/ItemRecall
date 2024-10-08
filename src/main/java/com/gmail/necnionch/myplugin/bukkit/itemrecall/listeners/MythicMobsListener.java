package com.gmail.necnionch.myplugin.bukkit.itemrecall.listeners;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.items.ItemManager;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class MythicMobsListener implements Listener {

    private final ItemManager items;

    public MythicMobsListener(MythicPlugin plugin) {
        this.items = plugin.getItemManager();
    }


    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();
        Item item = event.getItem();

        String mythicItemType = items.getMythicTypeFromItem(item.getItemStack());
        if (mythicItemType == null)
            return;




    }

}
