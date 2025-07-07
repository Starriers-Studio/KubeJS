package dev.latvian.mods.kubejs.holder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import me.textrue.kubejs.fabric.helper.RegistryHelper;
import me.textrue.kubejs.fabric.thirdparty.holdersets.HolderSetType;
import me.textrue.kubejs.fabric.thirdparty.holdersets.ICustomHolderSet;
import me.textrue.kubejs.fabric.thirdparty.registries.ThirdPartyRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;


public interface KubeJSHolderSets {
	//DeferredRegister<HolderSetType> REGISTRY = DeferredRegister.create(NeoForgeRegistries.Keys.HOLDER_SET_TYPES, KubeJS.MOD_ID);

	Holder<HolderSetType> REGEX = RegistryHelper.registerHolderSet("regex", () -> new HolderSetType() {
		@Override
		public <T> MapCodec<? extends ICustomHolderSet<T>> makeCodec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
			return RegExHolderSet.codec(registryKey);
		}

		@Override
		public <T> StreamCodec<RegistryFriendlyByteBuf, ? extends ICustomHolderSet<T>> makeStreamCodec(ResourceKey<? extends Registry<T>> registryKey) {
			return RegExHolderSet.streamCodec(registryKey);
		}
	});

	Holder<HolderSetType> NAMESPACE = RegistryHelper.registerHolderSet("namespace", () -> new HolderSetType() {
		@Override
		public <T> MapCodec<? extends ICustomHolderSet<T>> makeCodec(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderCodec, boolean forceList) {
			return NamespaceHolderSet.codec(registryKey);
		}

		@Override
		public <T> StreamCodec<RegistryFriendlyByteBuf, ? extends ICustomHolderSet<T>> makeStreamCodec(ResourceKey<? extends Registry<T>> registryKey) {
			return NamespaceHolderSet.streamCodec(registryKey);
		}
	});

	static void init() {
		RegistryHelper.HOLDER_SETS.forEach((id, holderSetType) -> {
			Registry.register(ThirdPartyRegistries.HOLDER_SET_TYPES, id, holderSetType);
		});
	}
}
