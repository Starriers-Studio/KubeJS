package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import me.textrue.kubejs.fabric.helper.RegistryHelper;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Supplier;

public interface KubeJSCreativeTabs {
	//DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KubeJS.MOD_ID);

	Supplier<CreativeModeTab> TAB = RegistryHelper.registerCreativeModeTab("tab", () -> FabricItemGroup.builder()
		.title(CommonProperties.get().getCreativeModeTabName())
		.icon(() -> {
			var is = ItemStack.OPTIONAL_CODEC.parse(RegistryAccessContainer.BUILTIN.json(), CommonProperties.get().creativeModeTabIcon).result().orElse(ItemStack.EMPTY);
			return is.isEmpty() ? Items.PURPLE_DYE.getDefaultInstance() : is;
		})
		.displayItems((params, output) -> {
			for (var b : RegistryObjectStorage.ITEM) {
				output.accept(b.get().getDefaultInstance());
			}
		})
		.build()
	);

	static void init() {
		RegistryHelper.CREATIVE_MODE_TABS.forEach((id, tab) -> {
			Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, tab);
		});
	}
}
