package me.textrue.kubejs.fabric.helper.key;

import com.mojang.blaze3d.platform.InputConstants;
import me.textrue.kubejs.fabric.thirdparty.settings.KeyConflictContext;

public class FabricAbstractKeyMapping extends AbstractKeyMapping {

	protected final FabricKeyMapping fabricMapping;

	public FabricAbstractKeyMapping(FabricKeyMapping fabricMapping, KeyConflictContext context) {
		super(context);
		this.fabricMapping = fabricMapping;
	}

	@Override
	public FabricKeyMapping getMapping() {
		return this.fabricMapping;
	}

	@Override
	public boolean isActiveAndMatches(InputConstants.Key key) {
		if (isUnbound()) {
			return false;
		}
		if (!this.fabricMapping.realKey.equals(key)) {
			return false;
		}
		return context.isActive();
	}
}
