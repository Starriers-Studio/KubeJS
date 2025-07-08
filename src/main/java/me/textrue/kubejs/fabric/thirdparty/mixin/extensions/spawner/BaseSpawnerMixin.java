package me.textrue.kubejs.fabric.thirdparty.mixin.extensions.spawner;

import com.mojang.datafixers.util.Either;
import me.textrue.kubejs.fabric.thirdparty.events.entity.living.LivingCheckSpawnEvent;
import me.textrue.kubejs.fabric.thirdparty.extensions.IOwnedSpawner;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BaseSpawner.class)
public class BaseSpawnerMixin implements IOwnedSpawner {

	@Redirect(
		method = "serverTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Mob;checkSpawnRules(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/MobSpawnType;)Z",
			ordinal = 0
		)
	)
	private boolean checkSpawnerSpawn(Mob mob, LevelAccessor level, MobSpawnType type) {
		var result = LivingCheckSpawnEvent.EVENT.invoker()
			.checkSpawn(mob, level, mob.getX(), mob.getY(), mob.getZ(), type, this);
		if (result.value() != null) {
			return result.value();
		}
		return mob.checkSpawnRules(level, type) && mob.checkSpawnObstruction(level);
	}

	@Override
	public @Nullable Either<BlockEntity, Entity> getOwner() {
		// The vanilla anonymous classes have proper overrides, but we return null here for compatibility.
		return null;
	}
}
