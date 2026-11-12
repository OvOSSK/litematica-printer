package me.aleksilassila.litematica.printer.integration.quickshulker;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;

/** Sends a click without predicting it locally, so changed slots are returned by the server. */
public final class QuickShulkerClicks {
    private QuickShulkerClicks() { }

    public static void send(AbstractContainerMenu menu, int slot, int button, ClickType type) {
        send(menu, menu.getStateId(), slot, button, type);
    }

    public static void requestFullSync(AbstractContainerMenu menu) {
        // Slot -1 PICKUP is a vanilla no-op (unlike -999, which drops the cursor).
        // Revisions are 0..32767. A mismatch forces a full authoritative snapshot.
        send(menu, -1, -1, 0, ClickType.PICKUP);
    }

    /** One click and one authoritative full reply, avoiding a second in-flight snapshot request. */
    public static void sendWithFullSync(AbstractContainerMenu menu, int slot, int button, ClickType type) {
        send(menu, -1, slot, button, type);
    }

    private static void send(AbstractContainerMenu menu, int stateId, int slot, int button, ClickType type) {
        var connection = Minecraft.getInstance().getConnection();
        if (connection == null) return;
        connection.send(new ServerboundContainerClickPacket(menu.containerId, stateId,
                slot, button, type, menu.getCarried().copy(), new Int2ObjectOpenHashMap<>()));
    }
}
