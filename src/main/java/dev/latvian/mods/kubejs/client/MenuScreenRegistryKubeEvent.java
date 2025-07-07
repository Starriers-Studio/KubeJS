package dev.latvian.mods.kubejs.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MenuScreenRegistryKubeEvent implements ClientKubeEvent {
	public MenuScreenRegistryKubeEvent() {
	}

	public void register(MenuType<?> type, MenuScreens.ScreenConstructor constructor) {
		MenuScreens.register(type, constructor);
	}
}
