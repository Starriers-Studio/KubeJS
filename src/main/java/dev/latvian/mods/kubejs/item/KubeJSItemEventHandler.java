package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.InventoryChangedKubeEvent;
import dev.latvian.mods.kubejs.plugin.builtin.event.ItemEvents;
import dev.latvian.mods.kubejs.plugin.builtin.event.PlayerEvents;
import me.textrue.kubejs.fabric.thirdparty.events.entity.player.ItemPickupEvents;
import me.textrue.kubejs.fabric.thirdparty.util.event.ThirdPartyEventResult;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class KubeJSItemEventHandler {

	public static void init() {
		UseItemCallback.EVENT.register(KubeJSItemEventHandler::rightClick);
		ItemPickupEvents.PRE.register(KubeJSItemEventHandler::itemPickupPre);
		ItemPickupEvents.POST.register(KubeJSItemEventHandler::itemPickupPost);
		AttackEntityCallback.EVENT.register(KubeJSItemEventHandler::entityInteract);
	}

	public static InteractionResultHolder<ItemStack> rightClick(Player player, Level world, InteractionHand hand) {
		var stack = player.getItemInHand(hand);
		var key = stack.getItem().kjs$getKey();

		if (ItemEvents.RIGHT_CLICKED.hasListeners(key) && !player.getCooldowns().isOnCooldown(stack.getItem())) {
			return new InteractionResultHolder<>(
				ItemEvents.RIGHT_CLICKED.post(player, key, new ItemClickedKubeEvent(player, hand, stack)).compoundResult().result().asMinecraft(), stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	public static ThirdPartyEventResult itemPickupPre(Player player, ItemEntity entity, ItemStack stack) {
		var key = entity.getItem().getItem().kjs$getKey();
		if (ItemEvents.CAN_PICK_UP.hasListeners(key)) {
			return ItemEvents.CAN_PICK_UP.post(player, key, new ItemPickedUpKubeEvent(player, entity, stack)).compoundResult().result();
		}
		return ThirdPartyEventResult.pass();
	}

	public static void itemPickupPost(Player player, ItemEntity entity, ItemStack stack) {
		var key = stack.getItem().kjs$getKey();
		if (ItemEvents.PICKED_UP.hasListeners(key)) {
			ItemEvents.PICKED_UP.post(player, key, new ItemPickedUpKubeEvent(player, entity, stack));
		}
	}

	public static ThirdPartyEventResult itemDrop(Player player, ItemEntity entity) {
		var key = entity.getItem().getItem().kjs$getKey();
		if (ItemEvents.DROPPED.hasListeners(key)) {
			return ItemEvents.DROPPED.post(player, key, new ItemDroppedKubeEvent(player, entity)).compoundResult().result();
		}
		return ThirdPartyEventResult.pass();
	}

	public static InteractionResult entityInteract(Player player, Level world, InteractionHand hand, Entity entity, EntityHitResult hitResult) {
		var stack = player.getItemInHand(hand);
		var key = stack.getItem().kjs$getKey();
		if (ItemEvents.ENTITY_INTERACTED.hasListeners(key)) {
			return ItemEvents.ENTITY_INTERACTED.post(entity, key, new ItemEntityInteractedKubeEvent(player, null, hand, stack)).compoundResult().result().asMinecraft();
		}
		return InteractionResult.PASS;
	}

	public static void crafted(Player player, ItemStack constructed, Container inventory) {
		if (!constructed.isEmpty()) {
			var key = constructed.getItem().kjs$getKey();

			if (ItemEvents.CRAFTED.hasListeners(key)) {
				ItemEvents.CRAFTED.post(player, key, new ItemCraftedKubeEvent(player, constructed, inventory));
			}

			if (PlayerEvents.INVENTORY_CHANGED.hasListeners(key)) {
				PlayerEvents.INVENTORY_CHANGED.post(player, key, new InventoryChangedKubeEvent(player, constructed, -1));
			}
		}
	}

	public static void smelted(Player player, ItemStack smelted) {
		if (!smelted.isEmpty()) {
			var key = smelted.getItem().kjs$getKey();

			if (ItemEvents.SMELTED.hasListeners(key)) {
				ItemEvents.SMELTED.post(player, key, new ItemSmeltedKubeEvent(player, smelted));
			}

			if (PlayerEvents.INVENTORY_CHANGED.hasListeners(key)) {
				PlayerEvents.INVENTORY_CHANGED.post(player, key, new InventoryChangedKubeEvent(player, smelted, -1));
			}
		}
	}

	public static void itemDestroyed(Player player, ItemStack destroyed, InteractionHand hand) {
		var key = destroyed.getItem().kjs$getKey();

		if (ItemEvents.ITEM_DESTROYED.hasListeners(key)) {
			ItemEvents.ITEM_DESTROYED.post(player, key, new ItemDestroyedKubeEvent(player, destroyed, hand));
		}
	}
}