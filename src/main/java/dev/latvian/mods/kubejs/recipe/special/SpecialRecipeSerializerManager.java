package dev.latvian.mods.kubejs.recipe.special;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SpecialRecipeSerializerManager implements KubeEvent {
	Event<Consumer<AfterPost>> AFTER_POST = EventFactory.createArrayBacked(Consumer.class, (callbacks) -> event -> {
		for (var callback : callbacks) {
			callback.accept(event);
		}
	});

	public static final class AfterPost {
	}

	public static final SpecialRecipeSerializerManager INSTANCE = new SpecialRecipeSerializerManager();
	private final Map<ResourceLocation, Boolean> data = new HashMap<>();

	public void reset() {
		synchronized (data) {
			data.clear();
		}
	}

	@Override
	public void afterPosted(EventResult result) {
		AFTER_POST.invoker().accept(new AfterPost());
	}

	public boolean isSpecial(Recipe<?> recipe) {
		return data.getOrDefault(BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.getSerializer()), recipe.isSpecial());
	}

	public void ignoreSpecialFlag(ResourceLocation id) {
		synchronized (data) {
			data.put(id, false);
		}
	}

	public void addSpecialFlag(ResourceLocation id) {
		synchronized (data) {
			data.put(id, true);
		}
	}

	public void ignoreSpecialMod(String modid) {
		synchronized (data) {
			for (var entry : BuiltInRegistries.RECIPE_SERIALIZER.entrySet()) {
				if (entry.getKey().location().getNamespace().equals(modid)) {
					data.put(entry.getKey().location(), false);
				}
			}
		}
	}

	public void addSpecialMod(String modid) {
		synchronized (data) {
			for (var entry : BuiltInRegistries.RECIPE_SERIALIZER.entrySet()) {
				if (entry.getKey().location().getNamespace().equals(modid)) {
					data.put(entry.getKey().location(), true);
				}
			}
		}
	}
}
