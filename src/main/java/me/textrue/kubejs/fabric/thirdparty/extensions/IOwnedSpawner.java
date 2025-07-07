package me.textrue.kubejs.fabric.thirdparty.extensions;

import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public interface IOwnedSpawner {
	@Nullable
	Either<BlockEntity, Entity> getOwner();
}
