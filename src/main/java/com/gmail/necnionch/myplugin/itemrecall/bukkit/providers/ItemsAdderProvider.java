package com.gmail.necnionch.myplugin.itemrecall.bukkit.providers;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.Item;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemProvider;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemResolver;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ItemsAdderProvider implements ItemProvider {

    public ItemsAdderProvider() {
    }

    public static @Nullable ItemsAdderProvider tryHook() {
        PluginManager pm = Bukkit.getPluginManager();

        if (pm.isPluginEnabled("ItemsAdder")) {
            try {
                Class.forName("dev.lone.itemsadder.api.CustomStack");
            } catch (ClassNotFoundException e) {
                return null;
            }
            return new ItemsAdderProvider();
        }
        return null;
    }


    @Nullable
    @Override
    public ItemResolver create(Item item) {
        return new Resolver(item.getName());
    }


    public static class Resolver implements ItemResolver {

        private final String namespacedId;

        public Resolver(String namespacedId) {
            this.namespacedId = namespacedId;
        }

        @Override
        public boolean matchItem(@NotNull ItemStack itemStack) {
            return Optional.ofNullable(CustomStack.byItemStack(itemStack))
                    .map(s -> namespacedId.equals(s.getNamespacedID()))
                    .orElse(false);
        }

        @Override
        public ItemStack createItem(@Nullable Player player, int amount) throws UnavailableError {
            return Optional.ofNullable(CustomStack.getInstance(namespacedId))
                    .map(CustomStack::getItemStack)
                    .map(is -> {
                        is.setAmount(amount);
                        return is;
                    })
                    .orElseThrow(UnavailableError::new);
        }
    }

}
