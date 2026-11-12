package neko.voidwarehouse.restock;

import net.minecraft.network.FriendlyByteBuf;
import neko.compat.StreamCodec;
import neko.compat.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Shared with litematica-printer; keep both copies identical. */
public record PrinterPlaceHelloResultPayload(int nonce, boolean enabled) implements CustomPacketPayload {
    public static final Type<PrinterPlaceHelloResultPayload> TYPE = new Type<>(
            new ResourceLocation("carpet_neko_addition", "printer_place_hello_result"));
    public static final StreamCodec<FriendlyByteBuf, PrinterPlaceHelloResultPayload> CODEC = new StreamCodec<>() {
        @Override
        public PrinterPlaceHelloResultPayload decode(FriendlyByteBuf buf) {
            return new PrinterPlaceHelloResultPayload(buf.readVarInt(), buf.readBoolean());
        }

        @Override
        public void encode(FriendlyByteBuf buf, PrinterPlaceHelloResultPayload payload) {
            buf.writeVarInt(payload.nonce());
            buf.writeBoolean(payload.enabled());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

