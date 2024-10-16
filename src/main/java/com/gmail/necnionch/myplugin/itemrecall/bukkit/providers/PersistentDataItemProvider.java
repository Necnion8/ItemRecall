package com.gmail.necnionch.myplugin.itemrecall.bukkit.providers;

import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.Item;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemProvider;
import com.gmail.necnionch.myplugin.itemrecall.bukkit.item.ItemResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public class PersistentDataItemProvider implements ItemProvider {

    public static PersistentDataItemProvider tryHook() {
        try {
            // supported since v1.14
            Class.forName("org.bukkit.persistence.PersistentDataContainer");
            ItemMeta.class.getMethod("getPersistentDataContainer");

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return null;
        }
        return new PersistentDataItemProvider();
    }

    @Nullable
    @Override
    public ItemResolver create(Item item) throws InvalidError {
        String name = item.getName();
        String[] sp = name.split(":", 3);
        String valueString = 3 <= sp.length ? sp[2] : null;

        try {
            //noinspection deprecation
            return new Resolver(new NamespacedKey(sp[0], sp[1]), valueString);
        } catch (IllegalArgumentException e) {
            throw new InvalidError(e.getMessage());
        }
    }


    public static class Resolver implements ItemResolver {

        private static final PersistentDataType<?, ?>[] PDC_TYPES = new PersistentDataType[] {
                PersistentDataType.STRING,
                PersistentDataType.BYTE,
                PersistentDataType.DOUBLE,
                PersistentDataType.FLOAT,
                PersistentDataType.INTEGER,
                PersistentDataType.LONG,
                PersistentDataType.SHORT,
        };

        private final NamespacedKey key;
        private final @Nullable String value;

        public Resolver(NamespacedKey key, @Nullable String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean matchItem(@NotNull ItemStack itemStack) {
            return Optional.ofNullable(itemStack.getItemMeta())
                    .map(PersistentDataHolder::getPersistentDataContainer)
                    .map(d -> Stream.of(PDC_TYPES).anyMatch(t -> d.has(key, t) && (value == null || value.equals(String.valueOf(d.get(key, t))))))
                    .orElse(false);
        }

        @Nullable
        @Override
        public ItemStack createItem(@Nullable Player player, int amount) throws UnavailableError {
            throw new UnavailableError("Cannot create item (test only)");
        }
    }
}
