package com.gmail.necnionch.myplugin.itemrecall.bukkit.item;

import org.jetbrains.annotations.Nullable;

public interface ItemProvider {
    @Nullable ItemResolver create(Item item) throws InvalidError;

    class InvalidError extends Exception {
        public InvalidError() {
            super();
        }

        public InvalidError(String message) {
            super(message);
        }
    }
}
