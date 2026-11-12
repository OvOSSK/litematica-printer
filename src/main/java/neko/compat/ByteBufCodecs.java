package neko.compat;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import java.util.*;
import java.util.function.Function;
public final class ByteBufCodecs {
 public static final StreamCodec<ByteBuf,Integer> VAR_INT = new StreamCodec<>() {
 public Integer decode(ByteBuf b) { return new FriendlyByteBuf(b).readVarInt(); }
 public void encode(ByteBuf b,Integer v) { new FriendlyByteBuf(b).writeVarInt(v); }};
 public static final StreamCodec<ByteBuf,Long> VAR_LONG = new StreamCodec<>() {
 public Long decode(ByteBuf b) { return new FriendlyByteBuf(b).readVarLong(); }
 public void encode(ByteBuf b,Long v) { new FriendlyByteBuf(b).writeVarLong(v); }};
 public static final StreamCodec<ByteBuf,String> STRING_UTF8 = new StreamCodec<>() {
 public String decode(ByteBuf b) { return new FriendlyByteBuf(b).readUtf(); }
 public void encode(ByteBuf b,String v) { new FriendlyByteBuf(b).writeUtf(v); }};
 public static <B extends ByteBuf,T> Function<StreamCodec<B,T>,StreamCodec<B,List<T>>> list(int max) {
 return element -> new StreamCodec<>() {
 public List<T> decode(B b) { int n=new FriendlyByteBuf(b).readVarInt(); if(n<0||n>max) throw new IllegalArgumentException("Invalid list size"); List<T> out=new ArrayList<>(n); for(int i=0;i<n;i++) out.add(element.decode(b)); return out; }
 public void encode(B b,List<T> v) { if(v.size()>max) throw new IllegalArgumentException("List too large"); new FriendlyByteBuf(b).writeVarInt(v.size()); for(T item:v) element.encode(b,item); }
 }; }
}
