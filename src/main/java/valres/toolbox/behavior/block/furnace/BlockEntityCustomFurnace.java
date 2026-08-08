package valres.toolbox.behavior.block.furnace;

import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequestSlotInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DropAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.SwapAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.TransferItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.packet.ContainerSetDataPacket;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityFurnace;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.level.particle.FlameParticle;
import org.powernukkitx.level.particle.SmokeParticle;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.RegisterException;
import org.powernukkitx.registry.Registries;
import valres.toolbox.menu.InventoryMenu;
import valres.toolbox.menu.MenuType;
import valres.toolbox.menu.transaction.MenuTransaction;
import valres.toolbox.menu.transaction.MenuTransactionDecision;

public final class BlockEntityCustomFurnace extends BlockEntityFurnace {
	public static final String TYPE = "toolbox:custom_furnace";
	private static final int VANILLA_COOK_DURATION = 200;

	private boolean loadingFuel;
	private final InventoryMenu inventoryMenu;

	public BlockEntityCustomFurnace(IChunk chunk, CompoundTag nbt) {
		super(chunk, nbt);
		this.inventoryMenu = InventoryMenu.create(MenuType.FURNACE).setName(this.getName()).bindInventory(this.inventory).setTransactionHandler(this::handleMenuTransaction);
	}

	public boolean openInventory(@NonNull Player player) {
		this.inventoryMenu.setName(this.getName()).synchronizeFromBacking();
		return this.inventoryMenu.send(player);
	}

	public static synchronized void ensureRegistered() {
		Class<? extends BlockEntity> registered = Registries.BLOCKENTITY.get(TYPE);
		if (registered == BlockEntityCustomFurnace.class) {
			return;
		}
		if (registered != null) {
			throw new IllegalStateException("Block entity type '" + TYPE + "' is already registered by " + registered.getName());
		}

		try {
			Registries.BLOCKENTITY.register(TYPE, BlockEntityCustomFurnace.class);
		} catch (RegisterException exception) {
			throw new IllegalStateException("Cannot register block entity type '" + TYPE + "'", exception);
		}
	}

	@Override public boolean isBlockEntityValid() {
		return this.getBlock() instanceof BlockCustomFurnace;
	}

	@Override protected @NonNull String getFurnaceName() {
		BlockCustomFurnace furnace = this.getFurnaceBlock();
		return furnace == null ? super.getFurnaceName() : furnace.getInventoryTitle();
	}

	@Override protected int getSpeedMultiplier() {
		if (this.loadingFuel) {
			return 1;
		}

		BlockCustomFurnace furnace = this.getFurnaceBlock();
		return furnace == null ? 1 : furnace.getCookingSpeedMultiplier();
	}

	@Override protected void checkFuel(Item fuel) {
		this.loadingFuel = true;
		try {
			super.checkFuel(fuel);
		} finally {
			this.loadingFuel = false;
		}
	}

	@Override protected void setBurning(boolean burning) {
		BlockCustomFurnace furnace = this.getFurnaceBlock();
		if (furnace == null || furnace.isLit() == burning) {
			return;
		}

		furnace.setLit(burning);
		this.getLevel().setBlock(this, furnace, true, true);
	}

	@Override public boolean onUpdate() {
		boolean scheduled = super.onUpdate();
		this.inventoryMenu.synchronizeFromBacking();
		this.sendMenuData();
		BlockCustomFurnace furnace = this.getFurnaceBlock();
		if (scheduled && furnace != null && furnace.isLit() && furnace.displaysBurningParticles()) {
			this.spawnBurningParticles(furnace.getBlockFace());
		}
		return scheduled;
	}

	@Override public void close() {
		if (!this.closed && this.inventoryMenu != null) {
			this.inventoryMenu.closeAll();
		}
		super.close();
	}

	@Override public @NonNull CompoundTag getSpawnCompound() {
		return super.getSpawnCompound().putString("id", BlockEntity.FURNACE);
	}

	private @Nullable BlockCustomFurnace getFurnaceBlock() {
		Block block = this.getBlock();
		return block instanceof BlockCustomFurnace furnace ? furnace : null;
	}

	private void spawnBurningParticles(@NonNull BlockFace front) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		if (random.nextInt(8) != 0) {
			return;
		}

		double sideOffset = random.nextDouble(-0.2, 0.2);
		double frontX = front.getXOffset();
		double frontZ = front.getZOffset();
		Vector3 position = this.add(0.5 + frontX * 0.52 + frontZ * sideOffset, 0.55 + random.nextDouble(-0.05, 0.05), 0.5 + frontZ * 0.52 - frontX * sideOffset);

		this.getLevel().addParticle(new SmokeParticle(position));
		if (random.nextBoolean()) {
			this.getLevel().addParticle(new FlameParticle(position));
		}
	}

	private @NonNull MenuTransactionDecision handleMenuTransaction(@NonNull Player player, @NonNull MenuTransaction transaction) {
		if (transaction.getSlot() != 2) {
			return transaction.allow();
		}
		if (!this.isResultSource(transaction.getAction())) {
			return transaction.cancel();
		}

		Item before = transaction.getItemClicked();
		if (!before.isNull() && !transaction.isAlreadyCancelled()) {
			this.server.getScheduler().scheduleTask(() -> {
				Item after = this.inventory.getResult();
				if (after.isNull() || after.getCount() < before.getCount() || !after.equals(before, true, true)) {
					this.dropXp();
				}
			});
		}
		return transaction.allow();
	}

	private boolean isResultSource(@NonNull ItemStackRequestAction action) {
		ItemStackRequestSlotInfo source = null;
		switch (action) {
			case TransferItemStackRequestAction transfer -> source = transfer.getSource();
			case SwapAction swap -> source = swap.getSource();
			case DropAction drop -> source = drop.getSource();
			default -> {
			}
		}
		return source != null && source.getFullContainerName().getContainerName() == ContainerEnumName.FURNACE_RESULT_CONTAINER;
	}

	private void sendMenuData() {
		int cookProgress = this.getMenuCookProgress();
		int remainingFuelTime = Math.max(0, this.burnTime);
		int maximumFuelTime = Math.max(0, this.maxTime);

		for (Player viewer : this.inventoryMenu.getInventory().getViewers()) {
			int windowId = viewer.getWindowId(this.inventoryMenu.getInventory());
			if (windowId < 0) {
				continue;
			}

			this.sendMenuData(viewer, windowId, ContainerSetDataPacket.FURNACE_TICK_COUNT, cookProgress);
			this.sendMenuData(viewer, windowId, ContainerSetDataPacket.FURNACE_LIT_TIME, remainingFuelTime);
			this.sendMenuData(viewer, windowId, ContainerSetDataPacket.FURNACE_LIT_DURATION, maximumFuelTime);
		}
	}

	private int getMenuCookProgress() {
		int speedMultiplier = Math.max(1, this.getSpeedMultiplier());
		int actualCookDuration = Math.max(1, VANILLA_COOK_DURATION / speedMultiplier);
		long scaledProgress = (long) Math.max(0, this.cookTime) * VANILLA_COOK_DURATION / actualCookDuration;
		return (int) Math.min(VANILLA_COOK_DURATION, scaledProgress);
	}

	private void sendMenuData(@NonNull Player viewer, int windowId, int property, int value) {
		ContainerSetDataPacket packet = new ContainerSetDataPacket();
		packet.setContainerID((byte) windowId);
		packet.setId(property);
		packet.setValue(value);
		viewer.sendPacket(packet);
	}
}
