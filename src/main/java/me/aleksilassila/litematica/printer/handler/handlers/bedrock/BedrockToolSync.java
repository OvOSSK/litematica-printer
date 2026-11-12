package me.aleksilassila.litematica.printer.handler.handlers.bedrock;

import me.aleksilassila.litematica.printer.runtime.RuntimeAccess;
import me.aleksilassila.litematica.printer.utils.InventoryUtils;
import me.aleksilassila.litematica.printer.utils.minecraft.NetworkUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Objects;

/** Waits for a native server player-tick boundary after a modern main-hand tool selection. */
final class BedrockToolSync {
    private final Minecraft client;
    private Object connection;
    private Object player;
    private Object level;
    private Object epoch;
    private Signature observed;
    private Signature confirmed;
    private Signature pending;
    private int pendingSequence = -1;

    BedrockToolSync(Minecraft client) { this.client = client; }

    private boolean bindContext() {
        var listener = this.client.getConnection();
        Object currentConnection = listener == null ? null : listener.getConnection();
        var runtime = RuntimeAccess.currentOrNull();
        Object currentEpoch = runtime == null ? null : runtime.epoch();
        if (this.connection != currentConnection || this.player != this.client.player
                || this.level != this.client.level || !Objects.equals(this.epoch, currentEpoch)) {
            this.reset();
            this.connection = currentConnection;
            this.player = this.client.player;
            this.level = this.client.level;
            this.epoch = currentEpoch;
        }
        return this.connection != null && this.player != null && this.level != null;
    }

    private Signature currentSignature() {
        if (this.client.player == null) return null;
        var inventory = this.client.player.getInventory();
        int slot = InventoryUtils.getSelectedSlot(inventory);
        if (!Inventory.isHotbarSlot(slot)) return null;
        var stack = inventory.getItem(slot);
        if (stack.isEmpty()) return null;
        var normalized = stack.copy();
        normalized.setCount(1);
        normalized.setDamageValue(0);
        return new Signature(slot, normalized);
    }

    void onMainHandSelection() {
        if (!this.bindContext()) {
            this.invalidateTool();
            this.observed = null;
            return;
        }
        var current = this.currentSignature();
        if (!same(this.observed, current)) {
            this.invalidateTool();
            this.observed = current;
        }
    }

    boolean prepare() {
        return true;
    }


    boolean isPending() {
        return false;
    }

    void acknowledged(Object connection, int sequence) {
    }

    void invalidateTool() {
        this.confirmed = null;
        this.pending = null;
        this.pendingSequence = -1;
    }

    void reset() {
        this.invalidateTool();
        this.observed = null;
        this.connection = null;
        this.player = null;
        this.level = null;
        this.epoch = null;
    }

    private static boolean same(Signature first, Signature second) {
        if (first == null || second == null) return first == second;
        return first.slot() == second.slot() && ItemStack.isSameItemSameTags(first.stack(), second.stack());
    }

    private record Signature(int slot, ItemStack stack) { }
}
