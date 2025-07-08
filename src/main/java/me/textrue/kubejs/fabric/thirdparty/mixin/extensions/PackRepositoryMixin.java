package me.textrue.kubejs.fabric.thirdparty.mixin.extensions;

import me.textrue.kubejs.fabric.thirdparty.extensions.PackRepositoryExtension;
import net.minecraft.server.packs.repository.PackRepository;

import net.minecraft.server.packs.repository.RepositorySource;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin implements PackRepositoryExtension {
	@Shadow
	@Final
	private Set<RepositorySource> sources;

	@Override
	public synchronized void kjs$addPackFinder(RepositorySource packFinder) {
		this.sources.add(packFinder);
	}
}
