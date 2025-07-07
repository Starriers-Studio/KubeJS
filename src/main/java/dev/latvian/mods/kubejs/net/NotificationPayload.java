package dev.latvian.mods.kubejs.net;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.NotificationToastData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record NotificationPayload(NotificationToastData data) implements CustomPacketPayload {
	public static final StreamCodec<RegistryFriendlyByteBuf, NotificationPayload> STREAM_CODEC = NotificationToastData.STREAM_CODEC.map(NotificationPayload::new, NotificationPayload::data);

	@Override
	public Type<?> type() {
		return KubeJSNet.NOTIFICATION;
	}

	public static void handle(NotificationPayload payload, ClientPlayNetworking.Context ctx) {
		ctx.client().execute(() -> {
			var p0 = KubeJS.PROXY.getClientPlayer();

			if (p0 != null) {
				p0.kjs$notify(payload.data);
			}
		});
	}
}