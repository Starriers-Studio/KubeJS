package me.textrue.kubejs.fabric.thirdparty.extensions;

import net.minecraft.server.packs.repository.RepositorySource;

public interface PackRepositoryExtension {
	void kjs$addPackFinder(RepositorySource packFinder);
}
