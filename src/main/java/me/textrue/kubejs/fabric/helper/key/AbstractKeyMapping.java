package me.textrue.kubejs.fabric.helper.key;

import com.mojang.blaze3d.platform.InputConstants;
import me.textrue.kubejs.fabric.thirdparty.settings.KeyConflictContext;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public abstract class AbstractKeyMapping {
	protected final KeyConflictContext context;

	public AbstractKeyMapping(KeyConflictContext context) {
		this.context = context;
	}

	protected abstract KeyMapping getMapping();

	protected abstract boolean isActiveAndMatches(InputConstants.Key key);

	public boolean isUnbound() {
		return this.getMapping().isUnbound();
	}

	public Component getTranslatedKeyMessage() {
		return this.getMapping().getTranslatedKeyMessage();
	}

	public AbstractKeyMapping register(Consumer<KeyMapping> registerMethod) {
		registerMethod.accept(this.getMapping());
		return this;
	}
}
