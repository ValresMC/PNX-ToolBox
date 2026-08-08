package valres.toolbox.menu.transaction;

import java.util.Objects;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.Player;
import org.powernukkitx.event.inventory.ItemStackRequestActionEvent;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import valres.toolbox.menu.InventoryMenu;

/**
 * Immutable menu-facing view of a PowerNukkitX item stack request action.
 */
public final class MenuTransaction {
	private final InventoryMenu menu;
	private final Player player;
	private final int slot;
	private final Item itemClicked;
	private final Item itemClickedWith;
	private final ItemStackRequestActionEvent event;

	public MenuTransaction(@NonNull InventoryMenu menu, @NonNull Player player, int slot, @NonNull Item itemClicked, @NonNull Item itemClickedWith, @NonNull ItemStackRequestActionEvent event) {
		this.menu = Objects.requireNonNull(menu, "Menu cannot be null");
		this.player = Objects.requireNonNull(player, "Player cannot be null");
		this.slot = slot;
		this.itemClicked = Objects.requireNonNull(itemClicked, "Clicked item cannot be null").clone();
		this.itemClickedWith = Objects.requireNonNull(itemClickedWith, "Cursor item cannot be null").clone();
		this.event = Objects.requireNonNull(event, "Item stack request event cannot be null");
	}

	public @NonNull InventoryMenu getMenu() {
		return this.menu;
	}

	public @NonNull Inventory getInventory() {
		return this.menu.getInventory();
	}

	public @NonNull Player getPlayer() {
		return this.player;
	}

	public int getSlot() {
		return this.slot;
	}

	public @NonNull Item getItemClicked() {
		return this.itemClicked.clone();
	}

	public @NonNull Item getItemClickedWith() {
		return this.itemClickedWith.clone();
	}

	public @NonNull ItemStackRequestAction getAction() {
		return this.event.getAction();
	}

	public @NonNull ItemStackRequestActionType getActionType() {
		return this.getAction().getType();
	}

	public @NonNull ItemStackRequestActionEvent getEvent() {
		return this.event;
	}

	public boolean isAlreadyCancelled() {
		return this.event.isCancelled();
	}

	public @NonNull MenuTransactionDecision allow() {
		return MenuTransactionDecision.ALLOW;
	}

	public @NonNull MenuTransactionDecision cancel() {
		return MenuTransactionDecision.CANCEL;
	}
}
