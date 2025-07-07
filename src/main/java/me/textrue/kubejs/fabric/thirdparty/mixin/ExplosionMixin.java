package me.textrue.kubejs.fabric.thirdparty.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.textrue.kubejs.fabric.thirdparty.events.FabricExplosionEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Explosion.class)
public class ExplosionMixin {

	@Shadow
	@Final
	private Level level;

	@Inject(method = "explode()V", at = @At(value = "NEW", target = "(DDD)Lnet/minecraft/world/phys/Vec3;", ordinal = 1))
	public void onDetonate(CallbackInfo ci, @Local List<Entity> list) {
		FabricExplosionEvents.DETONATE.invoker().onDetonate(this.level, (Explosion) (Object) this, list);
	}
}
