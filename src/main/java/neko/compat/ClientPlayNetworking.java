package neko.compat;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
public final class ClientPlayNetworking {
 public record Context(Minecraft client, net.minecraft.client.player.LocalPlayer player) { }
 public static boolean canSend(CustomPacketPayload.Type<?> type) {
  return net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.canSend(type.id());
 }
 public static <T extends CustomPacketPayload> void registerGlobalReceiver(CustomPacketPayload.Type<T> type, BiConsumer<T,Context> handler) {
  var codec=PayloadTypeRegistry.playS2C().codec(type);
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(type.id(),(client,connection,buf,sender)-> {
   T payload=codec.decode(buf);
   var context = new Context(client, client.player);
   client.execute(()->handler.accept(payload,context));
  });
 }
 public static void send(CustomPacketPayload payload) { sendTyped(payload,payload.type()); }
 @SuppressWarnings("unchecked") private static <T extends CustomPacketPayload> void sendTyped(CustomPacketPayload payload, CustomPacketPayload.Type<T> type) {
  var buf=PacketByteBufs.create();
  try { PayloadTypeRegistry.playC2S().codec(type).encode(buf,(T)payload); }
  catch(RuntimeException error) { buf.release(); throw error; }
  net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(type.id(),buf);
 }
}
