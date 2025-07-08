package me.textrue.kubejs.fabric.thirdparty.mixin.client.extensions;

import me.textrue.kubejs.fabric.thirdparty.extensions.RenderTypeExtension;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static net.minecraft.client.renderer.RenderType.chunkBufferLayers;

@Mixin(RenderType.class)
public abstract class RenderTypeMixin implements RenderTypeExtension {

	@Unique
	private int chunkLayerId = -1;

	@Contract(mutates = "this")
	@Override
	public final int getChunkLayerId() {
		int i = 0;
        for (var layer : chunkBufferLayers())
            chunkLayerId = i++;
		return chunkLayerId;
	}
}
