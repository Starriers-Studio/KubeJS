package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.client.highlight.KubedexPayloadHandler;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record RequestEntityKubedexPayload(int entityId, int flags) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, RequestEntityKubedexPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, RequestEntityKubedexPayload::entityId,
		ByteBufCodecs.VAR_INT, RequestEntityKubedexPayload::flags,
		RequestEntityKubedexPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.Kubedex.REQUEST_ENTITY;
	}

	public static void handle(RequestEntityKubedexPayload payload, ServerPlayNetworking.Context ctx) {
		if (ctx.player() instanceof ServerPlayer serverPlayer && serverPlayer.hasPermissions(2)) {
			ctx.server().execute(() -> KubedexPayloadHandler.entity(serverPlayer, payload.entityId, payload.flags));
		}
	}
}