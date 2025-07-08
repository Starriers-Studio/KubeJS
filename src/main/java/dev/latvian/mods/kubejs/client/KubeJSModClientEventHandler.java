package dev.latvian.mods.kubejs.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.highlight.HighlightRenderer;
import dev.latvian.mods.kubejs.fluid.FluidBlockBuilder;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.fluid.FluidTypeBuilder;
import dev.latvian.mods.kubejs.gui.KubeJSMenus;
import dev.latvian.mods.kubejs.gui.KubeJSScreen;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemModelPropertiesKubeEvent;
import dev.latvian.mods.kubejs.item.ModifyItemTooltipsKubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.ClientEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.ItemEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.KeyBindEvents;
import dev.latvian.mods.kubejs.registry.RegistryObjectStorage;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.text.tooltip.ItemTooltipData;
import dev.latvian.mods.kubejs.util.ID;
import dev.latvian.mods.kubejs.web.LocalWebServer;
import dev.latvian.mods.kubejs.web.WebServerProperties;
import me.textrue.kubejs.fabric.helper.key.KeyMappingHelper;
import me.textrue.kubejs.fabric.thirdparty.events.AddPackFindersEvent;
import me.textrue.kubejs.fabric.thirdparty.fluids.BaseFlowingFluid;
import me.textrue.kubejs.fabric.thirdparty.fluids.extensions.ClientFluidTypeExtensions;
import me.textrue.kubejs.fabric.thirdparty.settings.KeyConflictContext;
import me.textrue.kubejs.fabric.thirdparty.settings.KeyModifier;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KubeJSModClientEventHandler {

	public static void init() {
		AddPackFindersEvent.EVENT.register(KubeJSModClientEventHandler::addClientPacks);
		blockColors();
		itemColors();
		registerMenuScreens();
		registerRenderers();
		registerKeyMappings();
		CoreShaderRegistrationCallback.EVENT.register(KubeJSModClientEventHandler::registerCoreShaders);
		registerParticleProviders();
	}

	public static void setupClient() {
		KubeJS.PROXY = new KubeJSClient();
		setupClient0();
	}

	public static void addClientPacks(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.CLIENT_RESOURCES) {
			event.addRepositorySource(new KubeJSResourcePackFinder());
		}
	}

	private static void setupClient0() {
		if (!PlatformWrapper.isGeneratingData() && Minecraft.getInstance() != null && WebServerProperties.get().enabled) {
			LocalWebServer.start(Minecraft.getInstance(), true);
		}

		ItemEvents.MODEL_PROPERTIES.post(ScriptType.STARTUP, new ItemModelPropertiesKubeEvent());

		for (var builder : RegistryObjectStorage.BLOCK) {
			if (builder instanceof BlockBuilder b) {
				switch (b instanceof FluidBlockBuilder fb ? fb.fluidBuilder.fluidType.renderType : b.renderType) {
					// TODO: Move these to model json
					case CUTOUT -> BlockRenderLayerMap.INSTANCE.putBlock(b.get(), RenderType.cutout());
					case CUTOUT_MIPPED -> BlockRenderLayerMap.INSTANCE.putBlock(b.get(), RenderType.cutoutMipped());
					case TRANSLUCENT -> BlockRenderLayerMap.INSTANCE.putBlock(b.get(), RenderType.translucent());
				}
			}
		}

		for (var builder : RegistryObjectStorage.FLUID) {
			if (builder instanceof FluidBuilder b) {
				switch (b.fluidType.renderType) {
					case CUTOUT -> {
						BlockRenderLayerMap.INSTANCE.putFluid(b.get().getSource(), RenderType.cutout());
						BlockRenderLayerMap.INSTANCE.putFluid(b.get().getFlowing(), RenderType.cutout());
					}
					case CUTOUT_MIPPED -> {
						BlockRenderLayerMap.INSTANCE.putFluid(b.get().getSource(), RenderType.cutoutMipped());
						BlockRenderLayerMap.INSTANCE.putFluid(b.get().getFlowing(), RenderType.cutoutMipped());
					}
					case TRANSLUCENT -> {
						BlockRenderLayerMap.INSTANCE.putFluid(b.get().getSource(), RenderType.translucent());
						BlockRenderLayerMap.INSTANCE.putFluid(b.get().getFlowing(), RenderType.translucent());
					}
				}
			}
		}

		var list = new ArrayList<ItemTooltipData>();
		ItemEvents.MODIFY_TOOLTIPS.post(ScriptType.CLIENT, new ModifyItemTooltipsKubeEvent(list::add));
		KubeJSClient.clientItemTooltips = List.copyOf(list);
	}

	public static void blockColors() {
		for (var builder : RegistryObjectStorage.BLOCK) {
			if (builder instanceof BlockBuilder b && b.tint != null) {
				ColorProviderRegistry.BLOCK.register(new BlockTintFunctionWrapper(b.tint), b.get());
			}
		}
	}

	public static void itemColors() {
		for (var builder : RegistryObjectStorage.ITEM) {
			if (builder instanceof ItemBuilder b && b.tint != null) {
				ColorProviderRegistry.ITEM.register(new ItemTintFunctionWrapper(b.tint), b.get());
			}
		}
	}

	public static void registerMenuScreens() {
		MenuScreens.register(KubeJSMenus.MENU.get(), KubeJSScreen::new);
		ClientEvents.MENU_SCREEN_REGISTRY.post(ScriptType.STARTUP, new MenuScreenRegistryKubeEvent());
	}


	public static void registerRenderers() {
		ClientEvents.ENTITY_RENDERER_REGISTRY.post(ScriptType.STARTUP, new EntityRendererRegistryKubeEvent());
		ClientEvents.BLOCK_ENTITY_RENDERER_REGISTRY.post(ScriptType.STARTUP, new BlockEntityRendererRegistryKubeEvent());
	}

	public static void registerKeyMappings() {
		KeyBindingHelper.registerKeyBinding(HighlightRenderer.keyMapping = KeyMappingHelper.create("key.categories.kubejs", "key.kubejs.kubedex", InputConstants.Type.KEYSYM).setContext(KeyConflictContext.UNIVERSAL).setModifier(KeyModifier.NONE).build(GLFW.GLFW_KEY_K).getMapping());

		var kubeEvent = new KeybindRegistryKubeEvent();
		KeyBindEvents.REGISTRY.post(kubeEvent);

		for (var bind : kubeEvent.build()) {
			KeyBindingHelper.registerKeyBinding(bind.mapping);
		}

		KubeJSKeybinds.triggerReload();
	}

	public static void registerCoreShaders(CoreShaderRegistrationCallback.RegistrationContext context) throws IOException {
		context.register(ID.mc("kubejs/rendertype_highlight"), DefaultVertexFormat.POSITION_COLOR, shaderInstance -> HighlightRenderer.INSTANCE.highlightShader = shaderInstance);
	}

	public static void registerClientExtensions(Fluid fluid) {
		for (var builder : RegistryObjectStorage.FLUID_TYPE) {
			if (builder instanceof FluidTypeBuilder b) {
				if (fluid instanceof BaseFlowingFluid flowingFluid) {
					flowingFluid.setFluidTypeExtensions(new ClientFluidTypeExtensions() {
						@Override
						public ResourceLocation getStillTexture() {
							return b.actualStillTexture;
						}

						@Override
						public ResourceLocation getFlowingTexture() {
							return b.actualFlowingTexture;
						}

						@Override
						public ResourceLocation getOverlayTexture() {
							return b.blockOverlayTexture;
						}

						@Override
						@Nullable
						public ResourceLocation getRenderOverlayTexture(Minecraft mc) {
							return b.screenOverlayTexture;
						}
					});
				}
			}
		}
	}

	public static void registerParticleProviders() {
		if (ClientEvents.PARTICLE_PROVIDER_REGISTRY.hasListeners()) {
			ClientEvents.PARTICLE_PROVIDER_REGISTRY.post(new ParticleProviderRegistryKubeEvent());
		}
	}
}
