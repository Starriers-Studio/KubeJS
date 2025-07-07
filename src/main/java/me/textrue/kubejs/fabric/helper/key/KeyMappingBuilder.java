package me.textrue.kubejs.fabric.helper.key;

import com.mojang.blaze3d.platform.InputConstants;
import me.textrue.kubejs.fabric.thirdparty.settings.KeyConflictContext;
import me.textrue.kubejs.fabric.thirdparty.settings.KeyModifier;

public class KeyMappingBuilder {
	protected final String category;
	protected final String description;
	protected KeyConflictContext context = KeyConflictContext.UNIVERSAL;
	protected KeyModifier modifier = KeyModifier.NONE;

	protected KeyMappingBuilder(String category, String description) {
		this.category = category;
		this.description = description;
	}

	public static KeyMappingBuilder create(String category, String description) {
		return new KeyMappingBuilder(category, description);
	}

	public KeyMappingBuilder setContext(KeyConflictContext context) {
		this.context = context;
		return this;
	}

	public KeyMappingBuilder setModifier(KeyModifier modifier) {
		this.modifier = modifier;
		return this;
	}

	protected FabricAbstractKeyMapping buildMouse(int mouseButton) {
		FabricKeyMapping keyMapping = new FabricKeyMapping(
			description,
			InputConstants.Type.MOUSE,
			mouseButton,
			category,
			context
		);
		return new FabricAbstractKeyMapping(keyMapping, context);
	}

	public FabricAbstractKeyMapping buildKeyboardKey(int key) {
		FabricKeyMapping keyMapping = new FabricKeyMapping(
			description,
			InputConstants.Type.KEYSYM,
			key,
			category,
			context
		);
		return new FabricAbstractKeyMapping(keyMapping, context);
	}
}
