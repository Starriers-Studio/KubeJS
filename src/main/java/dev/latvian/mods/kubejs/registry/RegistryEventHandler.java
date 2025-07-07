package dev.latvian.mods.kubejs.registry;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.entity.AttributeBuilder;
import dev.latvian.mods.kubejs.plugin.builtin.event.StartupEvents;
import dev.latvian.mods.kubejs.script.ConsoleJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.apache.commons.lang3.function.Consumers;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RegistryEventHandler {

	static {
		registerAll();
		registerEntityAttributes();
	}

	@SuppressWarnings("unchecked")
	public static void registerAll() {
		CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> {
			for (ResourceKey<? extends Registry<?>> registryKey : BuiltInRegistries.REGISTRY.registryKeySet()) {
				// 强制类型转换，实际使用时需确保类型安全
				ResourceKey<Registry<Object>> objRegistryKey = (ResourceKey<Registry<Object>>) registryKey;
				handleRegistryEvent(objRegistryKey);
			}
		});
	}

	public static void registerEntityAttributes() {
		var objStorage = RegistryObjectStorage.of(Registries.ATTRIBUTE);
		var predicatePair = objStorage.objects.values().stream().filter(AttributeBuilder.class::isInstance).map(AttributeBuilder.class::cast).flatMap(b -> b.getPredicateList().stream().map(p -> Pair.of(p, BuiltInRegistries.ATTRIBUTE.wrapAsHolder(b.get())))).toList();
		var entityTypes = ImmutableList.copyOf(BuiltInRegistries.ENTITY_TYPE.holders().filter(entityTypeReference -> {
			return DefaultAttributes.hasSupplier(entityTypeReference.value());
		}).map(entityTypeReference -> {
			return (EntityType<? extends LivingEntity>) entityTypeReference.value();
		}).toList());

		for (EntityType<? extends LivingEntity> entityType : entityTypes) {
			predicatePair.stream().filter(p -> {
				return p.getFirst().test(entityType);
			}).forEach(holderPair -> {
				AttributeSupplier attributeSupplier = DefaultAttributes.getSupplier(entityType);

				Map<Holder<Attribute>, Double> attributeToBaseValueMap = BuiltInRegistries.ATTRIBUTE.holders()
					.filter(attributeSupplier::hasAttribute)
					.map(holder -> attributeSupplier.createInstance(Consumers.nop(), holder))
					.filter(Objects::nonNull)
					.collect(Collectors.toMap(AttributeInstance::getAttribute, AttributeInstance::getBaseValue));

				attributeToBaseValueMap.put(holderPair.getSecond(), holderPair.getSecond().value().getDefaultValue());
				AttributeSupplier.Builder builder = AttributeSupplier.builder();
				attributeToBaseValueMap.forEach(builder::add);
				FabricDefaultAttributeRegistry.register(entityType, builder.build());
			});
		}
	}

	private static <T> void handleRegistryEvent(ResourceKey<Registry<T>> registryKey) {
		StartupEvents.REGISTRY.post(ScriptType.STARTUP, (ResourceKey) registryKey, new RegistryKubeEvent<>(registryKey));

		var objStorage = RegistryObjectStorage.of(registryKey);

		if (objStorage.objects.isEmpty()) {
			if (DevProperties.get().logRegistryEventObjects) {
				KubeJS.LOGGER.info("Skipping " + registryKey.location() + " registry - no objects to build");
			}

			return;
		}

		if (DevProperties.get().logRegistryEventObjects) {
			KubeJS.LOGGER.info("Building " + objStorage.objects.size() + " objects of " + registryKey.location() + " registry");
		}

		int added = 0;

		for (var builder : objStorage) {
			if (!builder.dummyBuilder) {
				Registry.register((Registry<T>) BuiltInRegistries.REGISTRY.get(registryKey.registry()), builder.id, builder.createTransformedObject());

				if (DevProperties.get().logRegistryEventObjects) {
					ConsoleJS.STARTUP.info("+ " + registryKey.location() + " | " + builder.id);
				}

				added++;
			}
		}

		if (!objStorage.objects.isEmpty() && DevProperties.get().logRegistryEventObjects) {
			KubeJS.LOGGER.info("Registered " + added + "/" + objStorage.objects.size() + " objects of " + registryKey.location());
		}
	}
}
