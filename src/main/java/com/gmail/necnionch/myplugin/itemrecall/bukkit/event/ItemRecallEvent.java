package com.gmail.necnionch.myplugin.itemrecall.bukkit.event;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ReplaceItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemRecallEvent extends Event implements Cancellable {
    public static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;
    private final @Nullable Player player;
    private final @NotNull ReplaceItem replace;
    private final @NotNull ItemStack oldItemStack;
    private @Nullable ItemStack newItemStack;
    private final @Nullable Event baseEvent;

    public ItemRecallEvent(@Nullable Player player, @NotNull ReplaceItem replace, @NotNull ItemStack oldItemStack, @Nullable ItemStack newItemStack, @Nullable Event baseEvent) {
        this.player = player;
        this.replace = replace;
        this.oldItemStack = oldItemStack;
        this.newItemStack = newItemStack;
        this.baseEvent = baseEvent;
    }

    public @Nullable Player getPlayer() {
        return player;
    }

    public @NotNull ReplaceItem getReplace() {
        return replace;
    }

    public @NotNull ItemStack getOldItemStack() {
        return oldItemStack;
    }

    public @Nullable ItemStack getNewItemStack() {
        return newItemStack;
    }

    public void setNewItemStack(@Nullable ItemStack itemStack) {
        this.newItemStack = itemStack;
    }

    public @Nullable Event getBaseEvent() {
        return baseEvent;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
