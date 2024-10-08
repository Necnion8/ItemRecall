package com.gmail.necnionch.myplugin.itemrecall.bukkit.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemProvider {

    boolean matchItem(ItemStack itemStack);

    ItemStack createItem(Player player);

}
