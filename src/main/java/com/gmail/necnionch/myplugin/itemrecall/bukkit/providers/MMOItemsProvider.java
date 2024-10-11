package com.gmail.necnionch.myplugin.itemrecall.bukkit.providers;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.Item;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemProvider;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemResolver;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MMOItemsProvider implements ItemProvider {

    private final MMOItems mmoPlugin;

    public MMOItemsProvider(MMOItems mmoPlugin) {
        this.mmoPlugin = mmoPlugin;
    }

    public static @Nullable MMOItemsProvider tryHook() {
        PluginManager pm = Bukkit.getPluginManager();

        if (pm.isPluginEnabled("MMOItems")) {
            return new MMOItemsProvider(JavaPlugin.getPlugin(MMOItems.class));
        }
        return null;
    }


    @Nullable
    @Override
    public ItemResolver create(Item item) {
        String[] sp = item.getName().split(":", 2);
        return new Resolver(sp[0], sp[1]);
    }


    public class Resolver implements ItemResolver {

        private final String type;
        private final String id;

        public Resolver(String type, String id) {
            this.type = type;
            this.id = id;
        }

        @Override
        public boolean matchItem(@NotNull ItemStack itemStack) {
            String type = Optional.ofNullable(MMOItems.getType(itemStack)).map(Type::getId).orElse(null);
            String id = MMOItems.getID(itemStack);

            return this.type.equals(type) && this.id.equals(id);
        }

        @Override
        public ItemStack createItem(@Nullable Player player, int amount) throws UnavailableError {
            Type type = mmoPlugin.getTypes().get(this.type);
            MMOItem mmoItem = mmoPlugin.getMMOItem(type, this.id);
            if (mmoItem == null)
                throw new UnavailableError();

            ItemStack itemStack = mmoItem.newBuilder().build();
            if (itemStack == null)
                throw new UnavailableError();

            itemStack.setAmount(amount);
            return itemStack;
        }
    }

}
