package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.DirectionWrapper;
import me.textrue.kubejs.fabric.thirdparty.util.ThirdPartyContexts;
import me.textrue.kubejs.fabric.thirdparty.util.TransferUtil;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleSidedEnergyContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnergyStorageAttachment implements BlockEntityAttachment {
	public static final BlockEntityAttachmentType TYPE = new BlockEntityAttachmentType(KubeJS.id("energy_storage"), Factory.class);

	public record Factory(int capacity, Optional<Integer> maxReceive, Optional<Integer> maxExtract, Optional<Integer> autoOutput) implements BlockEntityAttachmentFactory {
		@Override
		public BlockEntityAttachment create(BlockEntityAttachmentInfo info, KubeBlockEntity entity) {
			int rx = Math.max(0, maxReceive.orElse(0));
			int tx = Math.max(0, maxExtract.orElse(0));
			int auto = Math.max(0, autoOutput.orElse(0));
			return new EnergyStorageAttachment(entity, capacity, rx, tx, auto, auto > 0 ? info.directions().isEmpty() ? DirectionWrapper.VALUES : info.directions().toArray(new Direction[0]) : DirectionWrapper.NONE);
		}

		@Override
		public boolean isTicking() {
			return autoOutput.isPresent() && autoOutput.get() > 0;
		}

		@Override
		public List<BlockApiLookup<?, ?>> getCapabilities() {
			BlockApiLookup<EnergyStorage, Direction> lookup = BlockApiLookup.get(ThirdPartyContexts.ENERGY_STORAGE_BLOCK_ID, EnergyStorage.class, Direction.class);
			return List.of(lookup);
		}
	}

	public static class Wrapped extends SimpleSidedEnergyContainer {
		private final EnergyStorageAttachment attachment;
		private final int capacity;
		private final int maxReceive;
		private final int maxExtract;

		public Wrapped(EnergyStorageAttachment attachment, int capacity, int maxReceive, int maxExtract) {
			//super(capacity, maxReceive, maxExtract);
			this.attachment = attachment;
			this.capacity = capacity;
			this.maxReceive = maxReceive;
			this.maxExtract = maxExtract;
		}

		public void setEnergyStored(int energy) {
			this.amount = Mth.clamp(energy, 0, capacity);
		}

		public int addEnergy(int add, boolean simulate) {
			int i = Math.toIntExact(Mth.clamp(this.capacity - this.amount, 0, add));

			if (!simulate && i > 0) {
				amount += i;
				attachment.entity.save();
			}

			return i;
		}

		public int removeEnergy(int remove, boolean simulate) {
			int i = Math.toIntExact(Math.max(amount, remove));

			if (!simulate && i > 0) {
				amount -= i;
				attachment.entity.save();
			}

			return i;
		}

		public boolean useEnergy(int use, boolean simulate) {
			if (amount >= use) {
				if (!simulate) {
					amount -= use;
					attachment.entity.save();
				}

				return true;
			}

			return false;
		}

		@Override
		public long getMaxExtract(@Nullable Direction side) {
			int s = Math.toIntExact(((SimpleSidedEnergyContainer) this).getMaxExtract(side));

			if (s > 0 && !attachment.entity.getLevel().isClientSide()) {
				attachment.entity.save();
			}

			return s;
		}

		@Override
		public long getMaxInsert(@Nullable Direction side) {
			int s = Math.toIntExact(((SimpleSidedEnergyContainer) this).getMaxInsert(side));

			if (s > 0 && !attachment.entity.getLevel().isClientSide()) {
				attachment.entity.save();
			}

			return s;
		}

		@Override
		public long getCapacity() {
			return 0;
		}
	}

	private final KubeBlockEntity entity;
	public final Wrapped energyStorage;
	public final int autoOutput;
	public final Direction[] autoOutputDirections;

	public EnergyStorageAttachment(KubeBlockEntity entity, int capacity, int maxReceive, int maxExtract, int autoOutput, Direction[] autoOutputDirections) {
		this.entity = entity;
		this.energyStorage = new Wrapped(this, capacity, maxReceive, maxExtract);
		this.autoOutput = autoOutput;
		this.autoOutputDirections = autoOutputDirections;
	}

	@Override
	public Object getWrappedObject() {
		return energyStorage;
	}

	@Override
	@Nullable
	public <CAP, SRC> CAP getCapability(BlockApiLookup<CAP, SRC> capability) {
		if (capability == BlockApiLookup.get(ThirdPartyContexts.ENERGY_STORAGE_BLOCK_ID, EnergyStorage.class, Direction.class)) {
			return (CAP) energyStorage;
		}

		return null;
	}

	@Override
	public void serverTick() {
		if (autoOutputDirections.length > 0 && autoOutput > 0) {
			var list = new ArrayList<EnergyStorage>(1);

			for (var dir : autoOutputDirections) {
				BlockApiLookup<EnergyStorage, Direction> lookup = BlockApiLookup.get(ThirdPartyContexts.ENERGY_STORAGE_BLOCK_ID, EnergyStorage.class, Direction.class);
				//var c = Capabilities.EnergyStorage.BLOCK.getCapability(entity.getLevel(), entity.getBlockPos().relative(dir), null, null, dir.getOpposite());
				var c = lookup.find(entity.getLevel(), entity.getBlockPos().relative(dir), dir.getOpposite());

				if (c != null && c != energyStorage) {
					list.add(c);
				}
			}

			if (!list.isEmpty()) {
				int draw = Math.toIntExact(Math.min(autoOutput, energyStorage.getCapacity()) / list.size());

				if (draw > 0) {
					for (var c : list) {
						int e = Math.toIntExact(c.extract(draw, TransferUtil.getTransaction()));

						if (e > 0) {
							c.extract(c.insert(e, TransferUtil.getTransaction()), TransferUtil.getTransaction());
						} else {
							break;
						}
					}
				}
			}
		}
	}
}
