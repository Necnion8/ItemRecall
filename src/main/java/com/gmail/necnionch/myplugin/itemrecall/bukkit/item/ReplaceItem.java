package com.gmail.necnionch.myplugin.itemrecall.bukkit.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ReplaceItem {

    private final @NotNull Item oldItem;
    private final @Nullable Item newItem;

    public ReplaceItem(@NotNull Item item, @Nullable Item newItem) {
        this.oldItem = item;
        this.newItem = newItem;
    }

    public @NotNull Item getOldItem() {
        return oldItem;
    }

    public @Nullable Item getNewItem() {
        return newItem;
    }


    public static ReplaceItem deserialize(Map<Object, Object> data) {
        Item newItem = null, oldItem = Item.deserialize((String) data.get("old"));

        if (data.containsKey("new")) {
            newItem = Item.deserialize((String) data.get("new"));
        }

        return new ReplaceItem(oldItem, newItem);
    }

}
