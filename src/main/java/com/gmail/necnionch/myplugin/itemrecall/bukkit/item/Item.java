package com.gmail.necnionch.myplugin.itemrecall.bukkit.item;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.ItemRecallPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Item {

    private final String type;
    private final String item;
    private @Nullable ItemResolver resolver;

    public Item(String type, String item, @Nullable ItemResolver resolver) {
        this.type = type;
        this.item = item;
        this.resolver = resolver;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return item;
    }

    @Nullable
    public ItemResolver getResolver() {
        if (resolver == null) {
            resolver = ItemRecallPlugin.createItemResolver(this);
        }
        return resolver;
    }

    public void setResolver(@Nullable ItemResolver resolver) {
        this.resolver = resolver;
    }


    public static Item deserialize(Map<String, String> data) {
        return new Item(data.get("type"), data.get("item"), null);
    }

    public static Item deserialize(String data) {
        String[] sp = data.split(":", 2);
        return new Item(sp[0], sp[1], null);
    }

}
