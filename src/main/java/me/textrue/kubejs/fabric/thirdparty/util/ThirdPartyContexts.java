package me.textrue.kubejs.fabric.thirdparty.util;

import me.textrue.kubejs.fabric.thirdparty.registries.ThirdPartyRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

public class ThirdPartyContexts {
	public static final boolean attributeAdvancedTooltipDebugInfo = false;

	public static final AABB AABB_INFINITE = new AABB(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

	public static final ResourceLocation FLUID_HANDLER_ITEM_ID = ThirdPartyRegistry.id("fluid_handler_item");
	public static final ResourceLocation FLUID_HANDLER_BLOCK_ID = ThirdPartyRegistry.id("fluid_handler_block");
	public static final ResourceLocation FLUID_HANDLER_ENTITY_ID = ThirdPartyRegistry.id("fluid_handler_entity");
	public static final ResourceLocation ENERGY_STORAGE_BLOCK_ID = ThirdPartyRegistry.id("energy_storage_block");
	public static final ResourceLocation ITEM_HANDLER_BLOCK_ID = ThirdPartyRegistry.id("item_handler_block");
}
