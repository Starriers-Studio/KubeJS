package me.textrue.kubejs.fabric.thirdparty.util;

import net.minecraft.client.renderer.RenderType;

public class RenderTypeHelper {
	public static RenderType getMovingBlockRenderType(RenderType renderType) {
		if (renderType == RenderType.translucent())
			return RenderType.translucentMovingBlock();
		return renderType;
	}
}
