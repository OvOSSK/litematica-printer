package neko.voidwarehouse.restock;

import net.minecraft.network.FriendlyByteBuf;
import neko.compat.StreamCodec;
import neko.compat.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Shared with litematica-printer; keep both copies identical. */
public record PrinterPlaceHelloPayload(int nonce) implements CustomPacketPayload {
    public static final Type<PrinterPlaceHelloPayload> TYPE = new Type<>(
            new ResourceLocation("carpet_neko_addition", "printer_place_hello"));
    public static final StreamCodec<FriendlyByteBuf, PrinterPlaceHelloPayload> CODEC = new StreamCodec<>() {
        @Override
        public PrinterPlaceHelloPayload decode(FriendlyByteBuf buf) {
            return new PrinterPlaceHelloPayload(buf.readVarInt());
        }

        @Override
        public void encode(FriendlyByteBuf buf, PrinterPlaceHelloPayload payload) {
            buf.writeVarInt(payload.nonce());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

