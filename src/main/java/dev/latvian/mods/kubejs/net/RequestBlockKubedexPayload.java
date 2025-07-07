package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.client.highlight.KubedexPayloadHandler;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record RequestBlockKubedexPayload(BlockPos pos, int flags) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, RequestBlockKubedexPayload> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, RequestBlockKubedexPayload::pos,
		ByteBufCodecs.VAR_INT, RequestBlockKubedexPayload::flags,
		RequestBlockKubedexPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.Kubedex.REQUEST_BLOCK;
	}

	public static void handle(RequestBlockKubedexPayload payload, ServerPlayNetworking.Context ctx) {
		if (ctx.player() instanceof ServerPlayer serverPlayer && serverPlayer.hasPermissions(2)) {
			ctx.server().execute(() -> KubedexPayloadHandler.block(serverPlayer, payload.pos, payload.flags));
		}
	}
}