package dev.latvian.mods.kubejs.client;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Consumer;

public class ParticleProviderRegistryKubeEvent implements ClientKubeEvent {
	public ParticleProviderRegistryKubeEvent() {
	}

	public <T extends ParticleOptions> void register(ParticleType<T> type, SpriteSetParticleProvider<T> spriteProvider) {
		ParticleFactoryRegistry.getInstance().register(type, spriteProvider::create);
	}

	public <T extends ParticleOptions> void register(ParticleType<T> type, Consumer<KubeAnimatedParticle> particle) {
		ParticleFactoryRegistry.getInstance().register(type, set -> (type1, level, x, y, z, xSpeed, ySpeed, zSpeed) -> {
			var kube = new KubeAnimatedParticle(level, x, y, z, set);
			kube.setParticleSpeed(xSpeed, ySpeed, zSpeed);
			particle.accept(kube);
			return kube;
		});
	}

	public <T extends ParticleOptions> void register(ParticleType<T> type) {
		register(type, p -> {
		});
	}

	public <T extends ParticleOptions> void registerSpecial(ParticleType<T> type, ParticleProvider<T> provider) {
		ParticleFactoryRegistry.getInstance().register(type, provider);
	}

	@FunctionalInterface
	public interface SpriteSetParticleProvider<T extends ParticleOptions> extends ParticleEngine.SpriteParticleRegistration<T> {
		Particle create(T type, ClientLevel clientLevel, double x, double y, double z, SpriteSet sprites, double xSpeed, double ySpeed, double zSpeed);

		@Override
		default ParticleProvider<T> create(SpriteSet sprites) {
			return (type, level, x, y, z, xSpeed, ySpeed, zSpeed) -> create(type, level, x, y, z, sprites, xSpeed, ySpeed, zSpeed);
		}
	}
}
