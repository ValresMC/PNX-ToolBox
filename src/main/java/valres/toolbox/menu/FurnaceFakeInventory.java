package valres.toolbox.menu;

import java.util.Optional;
import java.util.function.Consumer;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;
import org.powernukkitx.Player;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.inventory.fake.FakeInventory;
import org.powernukkitx.inventory.fake.FakeInventoryType;
import org.powernukkitx.inventory.fake.SingleFakeBlock;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Furnace fake inventory with independent actor identifier and display name.
 */
final class FurnaceFakeInventory extends FakeInventory {
	private final FurnaceFakeBlock fakeBlock = new FurnaceFakeBlock();
	private Consumer<Player> closeHandler;

	FurnaceFakeInventory(String title) {
		super(FakeInventoryType.FURNACE, title);
	}

	@Override public void onOpen(Player player) {
		player.setFakeInventoryOpen(true);
		this.fakeBlock.create(player, this.getTitle());

		// A network round-trip here can delay the UI by up to five ticks. Opening on
		// the next server tick still lets Bedrock receive the fake furnace first,
		// without tying menu responsiveness to the player's latency.
		player.getServer().getScheduler().scheduleTask(() -> this.finishOpening(player));
	}

	@Override public void onClose(Player player) {
		int windowId = player.getWindowId(this);
		if (windowId >= 0) {
			ContainerClosePacket packet = new ContainerClosePacket();
			packet.setContainerID((byte) windowId);
			packet.setServerInitiatedClose(player.getClosingWindowId() != windowId);
			packet.setContainerType(this.getType());
			player.sendPacket(packet);
		}

		this.viewers.remove(player);
		player.setFakeInventoryOpen(false);
		if (player.isOnline()) {
			player.waitForAck(() -> this.fakeBlock.remove(player));
		} else {
			this.fakeBlock.remove(player);
		}
		if (this.closeHandler != null) {
			this.closeHandler.accept(player);
		}
	}

	@Override public void setOnCloseHandler(Consumer<Player> onCloseHandler) {
		this.closeHandler = onCloseHandler;
	}

	private void finishOpening(Player player) {
		if (!player.isOnline() || player.getTopWindow().orElse(null) != this) {
			this.fakeBlock.remove(player);
			player.setFakeInventoryOpen(false);
			return;
		}

		Optional<Vector3> position = this.fakeBlock.getLastPositions(player).stream().findFirst();
		if (position.isEmpty()) {
			player.removeWindow(this);
			return;
		}

		Vector3 blockPosition = position.get();
		ContainerOpenPacket packet = new ContainerOpenPacket();
		packet.setContainerID((byte) player.getWindowId(this));
		packet.setContainerType(this.getType());
		packet.setPosition(Vector3i.from(blockPosition.getFloorX(), blockPosition.getFloorY(), blockPosition.getFloorZ()));
		player.sendPacket(packet);

		this.viewers.add(player);
		this.sendContents(player);
	}

	private static final class FurnaceFakeBlock extends SingleFakeBlock {
		private FurnaceFakeBlock() {
			super(BlockID.FURNACE, BlockEntity.FURNACE);
		}

		@Override protected CompoundTag getBlockEntityDataAt(Vector3 position, String title) {
			return BlockEntity.getDefaultCompound(position, BlockEntity.FURNACE).putBoolean("isMovable", true).putString("CustomName", title);
		}
	}
}
