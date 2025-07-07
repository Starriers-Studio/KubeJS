package me.textrue.kubejs.fabric.helper;

import net.minecraft.world.entity.item.ItemEntity;

public class ItemEntityHelper {

	private static int lifespan;

	public static void setLifespan(ItemEntity entity, int entityLifespan) {
		lifespan = entityLifespan;
	}

    public static int getLifespan(ItemEntity entity) {
    	return lifespan;
    }
}
