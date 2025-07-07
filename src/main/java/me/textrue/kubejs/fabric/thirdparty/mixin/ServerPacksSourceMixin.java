package me.textrue.kubejs.fabric.thirdparty.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.textrue.kubejs.fabric.thirdparty.events.AddPackFindersEvent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPacksSource.class)
public class ServerPacksSourceMixin {
	@ModifyReturnValue(method = "createPackRepository(Ljava/nio/file/Path;Lnet/minecraft/world/level/validation/DirectoryValidator;)Lnet/minecraft/server/packs/repository/PackRepository;", at = @At("RETURN"))
	private static PackRepository firePackFinders(PackRepository original) {
		AddPackFindersEvent.EVENT.invoker().findPacks(new AddPackFindersEvent(PackType.SERVER_DATA, original::kjs$addPackFinder, false));
		return original;
	}

	@ModifyReturnValue(method = "createVanillaTrustedRepository", at = @At("RETURN"))
	private static PackRepository firePackFinders2(PackRepository original) {
		AddPackFindersEvent.EVENT.invoker().findPacks(new AddPackFindersEvent(PackType.SERVER_DATA, original::kjs$addPackFinder, true));
		return original;
	}
}
