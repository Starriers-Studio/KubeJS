package me.textrue.kubejs.fabric.thirdparty.mixin;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlowingFluid.class)
public interface FlowingFluidAccessor {
	@Invoker
	boolean callCanConvertToSource(Level level);
}
