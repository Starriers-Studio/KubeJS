package me.textrue.kubejs.fabric.helper;

import me.textrue.kubejs.fabric.thirdparty.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class NetworkHelper {
	public static void sendToServer(CustomPacketPayload payload) {
		ClientPlayNetworking.send(payload);
	}

	public static void sendToAllPlayers(CustomPacketPayload payload) {
		var server = ServerLifecycleHooks.getCurrentServer();

		PlayerLookup.all(server).forEach(serverPlayer -> {
			sendToPlayer(serverPlayer, payload);
		});
	}

	public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
		ServerPlayNetworking.send(player, payload);
	}

	public static <T extends CustomPacketPayload> void playToClient(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ClientPlayNetworking.PlayPayloadHandler<T> handler) {
		PayloadTypeRegistry.playS2C().register(type, codec);
		ClientPlayNetworking.registerGlobalReceiver(type, handler);
	}

	public static <T extends CustomPacketPayload> void playToServer(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
		PayloadTypeRegistry.playC2S().register(type, codec);
		ServerPlayNetworking.registerGlobalReceiver(type, handler);
	}
}
