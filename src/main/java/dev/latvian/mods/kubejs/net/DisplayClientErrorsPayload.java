package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DisplayClientErrorsPayload() implements CustomPacketPayload {
	public static final StreamCodec<ByteBuf, DisplayClientErrorsPayload> STREAM_CODEC = StreamCodec.unit(new DisplayClientErrorsPayload());

	@Override
	public Type<?> type() {
		return KubeJSNet.DISPLAY_CLIENT_ERRORS;
	}

	public static void handle(DisplayClientErrorsPayload payload, ClientPlayNetworking.Context ctx) {
		ctx.client().execute(() -> KubeJS.PROXY.openErrors(ScriptType.CLIENT));
	}
}