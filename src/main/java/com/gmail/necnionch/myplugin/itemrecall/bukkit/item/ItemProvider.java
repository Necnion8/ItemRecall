package com.gmail.necnionch.myplugin.itemrecall.bukkit.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemProvider {

    boolean matchItem(@NotNull ItemStack itemStack);

    ItemStack createItem(@Nullable Player player);

}
