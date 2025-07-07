package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record DisplayServerErrorsPayload(int scriptType, List<ConsoleLine> errors, List<ConsoleLine> warnings) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, DisplayServerErrorsPayload> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, DisplayServerErrorsPayload::scriptType,
		ByteBufCodecs.collection(ArrayList::new, ConsoleLine.STREAM_CODEC), DisplayServerErrorsPayload::errors,
		ByteBufCodecs.collection(ArrayList::new, ConsoleLine.STREAM_CODEC), DisplayServerErrorsPayload::warnings,
		DisplayServerErrorsPayload::new
	);

	@Override
	public Type<?> type() {
		return KubeJSNet.DISPLAY_SERVER_ERRORS;
	}

	public static void handle(DisplayServerErrorsPayload payload, ClientPlayNetworking.Context ctx) {
		ctx.client().execute(() -> KubeJS.PROXY.openErrors(ScriptType.values()[payload.scriptType], payload.errors, payload.warnings));
	}
}