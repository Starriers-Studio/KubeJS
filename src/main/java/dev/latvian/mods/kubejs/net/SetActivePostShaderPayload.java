package dev.latvian.mods.kubejs.net;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record SetActivePostShaderPayload(Optional<ResourceLocation> id) implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, SetActivePostShaderPayload> STREAM_CODEC = ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC).map(SetActivePostShaderPayload::new, SetActivePostShaderPayload::id);

	@Override
	public Type<?> type() {
		return KubeJSNet.SET_ACTIVE_POST_SHADER;
	}

	public static void handle(SetActivePostShaderPayload payload, ClientPlayNetworking.Context ctx) {
		ctx.client().execute(() -> ctx.player().kjs$setActivePostShader(payload.id.orElse(null)));
	}
}