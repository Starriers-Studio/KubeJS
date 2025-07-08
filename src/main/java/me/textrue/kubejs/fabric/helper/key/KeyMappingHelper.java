package me.textrue.kubejs.fabric.helper.key;

import com.mojang.blaze3d.platform.InputConstants;
import me.textrue.kubejs.fabric.thirdparty.settings.KeyConflictContext;
import me.textrue.kubejs.fabric.thirdparty.settings.KeyModifier;

public class KeyMappingHelper {
	protected final String category;
	protected final String description;
	protected final InputConstants.Type inputType;
	protected KeyConflictContext context = KeyConflictContext.UNIVERSAL;
	protected KeyModifier modifier = KeyModifier.NONE;

	protected KeyMappingHelper(String category, String description, InputConstants.Type inputType) {
		this.category = category;
		this.description = description;
		this.inputType = inputType;
	}

	public static KeyMappingHelper create(String category, String description, InputConstants.Type inputType) {
		return new KeyMappingHelper(category, description, inputType);
	}

	public KeyMappingHelper setContext(KeyConflictContext context) {
		this.context = context;
		return this;
	}

	public KeyMappingHelper setModifier(KeyModifier modifier) {
		this.modifier = modifier;
		return this;
	}

	public FabricAbstractKeyMapping build(int button) {
		FabricKeyMapping keyMapping = new FabricKeyMapping(
			description,
			inputType,
			button,
			category,
			context
		);
		return new FabricAbstractKeyMapping(keyMapping, context);
	}
}
