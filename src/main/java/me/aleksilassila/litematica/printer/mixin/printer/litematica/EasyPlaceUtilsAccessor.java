package me.aleksilassila.litematica.printer.mixin.printer.litematica;

import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = fi.dy.masa.litematica.util.WorldUtils.class, remap = false)
public interface EasyPlaceUtilsAccessor {
    @Invoker("setEasyPlaceLastPickBlockTime")
    static void callSetEasyPlaceLastPickBlockTime() {
        throw new UnsupportedOperationException();
    }

    @Invoker("easyPlaceIsPositionCached")
    static boolean callEasyPlaceIsPositionCached(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Invoker("easyPlaceIsTooFast")
    static boolean callEasyPlaceIsTooFast() {
        throw new UnsupportedOperationException();
    }

}

