package com.gmail.necnionch.myplugin.itemrecall.bukkit.item;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.ItemRecallPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Item {

    private final String type;
    private final String item;
    private @Nullable ItemProvider provider;

    public Item(String type, String item, @Nullable ItemProvider provider) {
        this.type = type;
        this.item = item;
        this.provider = provider;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return item;
    }

    @Nullable
    public ItemProvider getProvider() {
        if (provider == null) {
            provider = ItemRecallPlugin.getItemProviderOfType(type);
        }
        return provider;
    }

    public void setProvider(@Nullable ItemProvider provider) {
        this.provider = provider;
    }


    public static Item deserialize(Map<String, String> data) {
        return new Item(data.get("type"), data.get("item"), null);
    }

    public static Item deserialize(String data) {
        String[] sp = data.split(":", 2);
        return new Item(sp[0], sp[1], null);
    }

}
