package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SendDataFromServerPayload(String channel, CompoundTag data) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, SendDataFromServerPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, SendDataFromServerPayload::channel,
		ByteBufCodecs.COMPOUND_TAG, SendDataFromServerPayload::data,
		SendDataFromServerPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.SEND_DATA_FROM_SERVER;
	}

	public static void handle(SendDataFromServerPayload payload, ClientPlayNetworking.Context ctx) {
		if (!payload.channel.isEmpty()) {
			ctx.client().execute(() -> KubeJS.PROXY.handleDataFromServerPacket(payload.channel, payload.data));
		}
	}
}