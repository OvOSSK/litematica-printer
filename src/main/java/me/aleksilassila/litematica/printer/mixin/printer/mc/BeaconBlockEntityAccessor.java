package me.aleksilassila.litematica.printer.mixin.printer.mc;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(BeaconBlockEntity.class)
public interface BeaconBlockEntityAccessor {
    @Accessor("BEACON_EFFECTS")
    static MobEffect[][] litematica_printer$getBeaconEffects() {
        throw new AssertionError();
    }

    @Mutable
    @Accessor("BEACON_EFFECTS")
    static void litematica_printer$setBeaconEffects(
            MobEffect[][] beaconEffects
    ) {
        throw new AssertionError();
    }
}
