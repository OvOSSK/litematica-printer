package me.aleksilassila.litematica.printer.interaction;

import net.fabricmc.fabric.mixin.content.registry.AxeItemAccessor;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashMap;
import java.util.Map;

public final class StrippableBlockLookup {
    private StrippableBlockLookup() {
    }

    public static Map<Block, Block> strippables() {
        return AxeItemAccessor.getStrippedBlocks();
    }
}
