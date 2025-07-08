package me.textrue.kubejs.fabric.thirdparty.mixin.extensions.spawner;

import com.mojang.datafixers.util.Either;
import me.textrue.kubejs.fabric.thirdparty.extensions.IOwnedSpawner;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TrialSpawner.class)
public class TrialSpawnerMixin implements IOwnedSpawner {
	@Shadow
	@Final
	private TrialSpawner.StateAccessor stateAccessor;

	@Override
	public @Nullable Either<BlockEntity, Entity> getOwner() {
		if (this.stateAccessor instanceof TrialSpawnerBlockEntity be) {
            return Either.left(be);
        }
        return null;
	}
}
