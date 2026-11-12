package me.aleksilassila.litematica.printer.utils.minecraft;

import me.aleksilassila.litematica.printer.config.Configs;
import me.aleksilassila.litematica.printer.mixin.printer.mc.BeaconBlockEntityAccessor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;


public final class BeaconEffectSync {
    private BeaconEffectSync() {
    }

    @SuppressWarnings("FieldCanBeLocal")
    private static boolean originalCaptured;

    private static MobEffect[][] originalBeaconEffects;
    private static final MobEffect[][] UNLOCKED_BEACON_EFFECTS = new MobEffect[][]{
            {MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED},
            {MobEffects.REGENERATION, MobEffects.DAMAGE_RESISTANCE},
            {MobEffects.JUMP, MobEffects.DAMAGE_BOOST},
            {}
    };

    public static void syncFromConfig() {
        if (!originalCaptured) {
            originalBeaconEffects = BeaconBlockEntityAccessor.litematica_printer$getBeaconEffects();
            originalCaptured = true;
        }

        if (Configs.Special.UNLOCK_BEACON_EFFECTS.getBooleanValue()) {
            if (BeaconBlockEntityAccessor.litematica_printer$getBeaconEffects() != UNLOCKED_BEACON_EFFECTS) {
                BeaconBlockEntityAccessor.litematica_printer$setBeaconEffects(UNLOCKED_BEACON_EFFECTS);
            }
            return;
        }

        if (BeaconBlockEntityAccessor.litematica_printer$getBeaconEffects() != originalBeaconEffects) {
            BeaconBlockEntityAccessor.litematica_printer$setBeaconEffects(originalBeaconEffects);
        }
    }
}
