package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.plugin.builtin.event.NetworkEvents;
import dev.latvian.mods.kubejs.script.ScriptType;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record SendDataFromClientPayload(String channel, CompoundTag data) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, SendDataFromClientPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, SendDataFromClientPayload::channel,
		ByteBufCodecs.COMPOUND_TAG, SendDataFromClientPayload::data,
		SendDataFromClientPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.SEND_DATA_FROM_CLIENT;
	}

	public static void handle(SendDataFromClientPayload payload, ServerPlayNetworking.Context context) {
		if (!payload.channel.isEmpty() && context.player() instanceof ServerPlayer serverPlayer && NetworkEvents.DATA_RECEIVED.hasListeners(payload.channel)) {
			context.server().execute(() -> NetworkEvents.DATA_RECEIVED.post(ScriptType.SERVER, payload.channel, new NetworkKubeEvent(serverPlayer, payload.channel, payload.data)));
		}
	}
}