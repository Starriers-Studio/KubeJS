package me.textrue.kubejs.fabric.thirdparty.events;

import me.textrue.kubejs.fabric.thirdparty.util.event.CompoundEventResult;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screens.Screen;

public class FabricScreenEvents {

	public static final Event<SetScreen> SET_SCREEN = EventFactory.createArrayBacked(SetScreen.class, callbacks -> screen -> {
		for (SetScreen callback : callbacks) {
			return callback.modifyScreen(screen);
		}
		return CompoundEventResult.pass();
	});

	@Environment(EnvType.CLIENT)
	public interface SetScreen {
		/**
		 * Invoked before a new screen is set to open.
		 * Equivalent to Forge's {@code GuiOpenEvent} event.
		 *
		 * @param screen The screen that is going to be opened.
		 * @return A {@link CompoundEventResult} determining the outcome of the event,
		 * if an outcome is set, the vanilla screen is overridden.
		 */
		CompoundEventResult<Screen> modifyScreen(Screen screen);
	}
}
