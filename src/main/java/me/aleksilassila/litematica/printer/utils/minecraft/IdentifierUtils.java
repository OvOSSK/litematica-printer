package me.aleksilassila.litematica.printer.utils.minecraft;

import net.minecraft.resources.ResourceLocation;

public class IdentifierUtils {

    public static ResourceLocation of(String string) {
        return new ResourceLocation(string);
    }

    public static ResourceLocation of(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }
}

