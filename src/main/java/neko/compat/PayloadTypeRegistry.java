package neko.compat;
import java.util.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
public final class PayloadTypeRegistry {
 private static final PayloadTypeRegistry C2S=new PayloadTypeRegistry(), S2C=new PayloadTypeRegistry();
 private final Map<ResourceLocation,StreamCodec<FriendlyByteBuf,?>> codecs=new HashMap<>();
 public static PayloadTypeRegistry playC2S(){return C2S;}
 public static PayloadTypeRegistry playS2C(){return S2C;}
 public <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type,StreamCodec<FriendlyByteBuf,T> codec){ if(codecs.putIfAbsent(type.id(),codec)!=null) throw new IllegalArgumentException("Duplicate channel"); }
 @SuppressWarnings("unchecked") public <T extends CustomPacketPayload> StreamCodec<FriendlyByteBuf,T> codec(CustomPacketPayload.Type<T> type){return (StreamCodec<FriendlyByteBuf,T>)Objects.requireNonNull(codecs.get(type.id()),"Unregistered channel");}
}
