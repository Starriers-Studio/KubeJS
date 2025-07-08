package me.textrue.kubejs.fabric.thirdparty.mixin.accessors;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccessor {
	@Accessor("resources")
	MinecraftServer.ReloadableResources getServerResources();
}
