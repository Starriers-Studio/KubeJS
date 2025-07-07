package me.textrue.kubejs.fabric.thirdparty.mixin;


import dev.latvian.mods.kubejs.player.KubeJSPlayerEventHandler;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.PlayerLoggedEvents;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.PlayerRespawnEvent;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {
	@Inject(method = "placeNewPlayer", at = @At("RETURN"))
	private void playerLoggedIn(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
		PlayerLoggedEvents.LOGGED_IN.invoker().login(serverPlayer);
	}

	@Inject(method = "remove", at = @At("HEAD"))
	private void playerLoggedOut(ServerPlayer serverPlayer, CallbackInfo ci) {
		PlayerLoggedEvents.LOGGED_OUT.invoker().logout(serverPlayer);
	}

	@Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 5))
	private void syncDatapackRegistries(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
		KubeJSPlayerEventHandler.datapackSync((PlayerList) (Object) this, player);
	}

	@Inject(method = "respawn", at = @At("RETURN"))
	private void playerRespawn(ServerPlayer serverPlayer, boolean bl, Entity.RemovalReason removalReason, CallbackInfoReturnable<ServerPlayer> cir) {
		PlayerRespawnEvent.EVENT.invoker().respawn(cir.getReturnValue(), bl, removalReason);
	}
}
