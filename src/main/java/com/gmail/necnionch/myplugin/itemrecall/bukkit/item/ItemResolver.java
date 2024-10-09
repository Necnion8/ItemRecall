package com.gmail.necnionch.myplugin.itemrecall.bukkit.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemResolver {

    boolean matchItem(@NotNull ItemStack itemStack);

    @Nullable ItemStack createItem(@Nullable Player player, int amount) throws UnavailableError;


    class UnavailableError extends Exception {
        public UnavailableError() {
            super("Unknown item");
        }

        public UnavailableError(String message) {
            super(message);
        }

    }
}
