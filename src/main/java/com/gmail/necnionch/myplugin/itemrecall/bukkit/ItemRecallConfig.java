package com.gmail.necnionch.myplugin.itemrecall.bukkit;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.Item;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ReplaceItem;
import com.gmail.necnionch.myplugin.itemrecall.common.BukkitConfigDriver;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemRecallConfig extends BukkitConfigDriver {

    private boolean enableLogging;
    private boolean enableDebug;
    private final List<ReplaceItem> items = Lists.newArrayList();
    private final Multimap<String, ReplaceItem> itemsOfOldType = ArrayListMultimap.create();  // cache

    public ItemRecallConfig(JavaPlugin plugin) {
        super(plugin);
    }


    @Override
    public boolean save() {
        return super.save();
    }


    @Override
    public boolean onLoaded(FileConfiguration config) {
        enableLogging = config.getBoolean("enable-logging", false);
        enableDebug = config.getBoolean("enable-debug", false);
        serializeItems();
        fillProviders();
        getLogger().info("Loaded " + items.size() + " items");
        return true;
    }

    private void serializeItems() {
        items.clear();
        itemsOfOldType.clear();

        List<?> cItems = config.getList("items");
        if (cItems == null)
            return;

        for (Object obj : cItems) {
            ReplaceItem replaceItem;

            if (obj instanceof String) {
                // remove only
                try {
                    replaceItem = new ReplaceItem(Item.deserialize(((String) obj)), null);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

            } else if (obj instanceof Map && ((Map<?, ?>) obj).containsKey("old")) {
                try {
                    //noinspection unchecked
                    replaceItem = ReplaceItem.deserialize((Map<Object, Object>) obj);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            } else {
                continue;
            }

            items.add(replaceItem);
            itemsOfOldType.put(replaceItem.getOldItem().getType(), replaceItem);
        }

    }

    private void fillProviders() {
        for (ReplaceItem replaceItem : items) {
            replaceItem.getOldItem().getResolver();
            Optional.ofNullable(replaceItem.getNewItem())
                    .ifPresent(Item::getResolver);
        }
    }


    public boolean isEnableLogging() {
        return enableLogging;
    }

    public boolean isEnableDebug() {
        return enableDebug;
    }

    public List<ReplaceItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<ReplaceItem> getItemsOfOldType(String type) {
        return Collections.unmodifiableList((List<ReplaceItem>) itemsOfOldType.get(type));
    }

}
