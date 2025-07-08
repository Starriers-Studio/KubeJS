package me.textrue.kubejs.fabric.thirdparty.extensions;

public interface RenderTypeExtension {
	default int getChunkLayerId() {
		return 0;
	}
}
