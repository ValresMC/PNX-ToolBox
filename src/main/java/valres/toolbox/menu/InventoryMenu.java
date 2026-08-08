package valres.toolbox.menu;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.Player;
import org.powernukkitx.event.inventory.ItemStackRequestActionEvent;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.inventory.InventoryListener;
import org.powernukkitx.inventory.fake.FakeInventory;
import org.powernukkitx.item.Item;
import valres.toolbox.menu.transaction.MenuTransaction;
import valres.toolbox.menu.transaction.MenuTransactionDecision;

/**
 * Extensible menu facade built on PowerNukkitX's native fake inventory API.
 */
public class InventoryMenu {
	private final MenuType type;
	private final FakeInventory inventory;

	private String name;
	private MenuTransactionHandler transactionHandler;
	private MenuCloseHandler closeHandler;
	private Inventory backingInventory;
	private final InventoryListener backingListener = this::copyBackingSlot;
	private boolean synchronizing;

	public static @NonNull InventoryMenu create(@NonNull MenuType type) {
		return new InventoryMenu(type);
	}

	public static @NonNull MenuTransactionHandler readOnly() {
		return (player, transaction) -> transaction.cancel();
	}

	public static @NonNull MenuTransactionHandler readOnly(@NonNull Consumer<MenuTransaction> observer) {
		Objects.requireNonNull(observer, "Transaction observer cannot be null");

		return (player, transaction) -> {
			observer.accept(transaction);
			return transaction.cancel();
		};
	}

	public InventoryMenu(@NonNull MenuType type) {
		this(type, Objects.requireNonNull(type, "Menu type cannot be null").getDefaultName());
	}

	public InventoryMenu(@NonNull MenuType type, @NonNull String name) {
		this.type = Objects.requireNonNull(type, "Menu type cannot be null");
		this.name = Objects.requireNonNull(name, "Menu name cannot be null");
		this.inventory = this.type.createInventory(this.name);
		this.inventory.setDefaultItemHandler(this::dispatchTransaction);
		this.inventory.setOnCloseHandler(this::onClose);
		this.inventory.addListener(this::copyMenuSlot);
	}

	public @NonNull MenuType getType() {
		return this.type;
	}

	public @NonNull String getName() {
		return this.name;
	}

	public @NonNull InventoryMenu setName(@NonNull String name) {
		this.name = Objects.requireNonNull(name, "Menu name cannot be null");
		this.inventory.setTitle(this.name);

		return this;
	}

	public @NonNull Inventory getInventory() {
		return this.inventory;
	}

	/**
	 * Uses another inventory as live storage while retaining this menu's fake
	 * Bedrock container.
	 */
	public @NonNull InventoryMenu bindInventory(@NonNull Inventory backingInventory) {
		Objects.requireNonNull(backingInventory, "Backing inventory cannot be null");
		if (backingInventory == this.inventory) {
			throw new IllegalArgumentException("A menu cannot bind its own inventory");
		}
		if (backingInventory.getSize() != this.inventory.getSize()) {
			throw new IllegalArgumentException("Backing inventory size must be " + this.inventory.getSize());
		}
		if (this.backingInventory == backingInventory) {
			return this;
		}
		if (this.backingInventory != null) {
			this.backingInventory.removeListener(this.backingListener);
		}

		this.backingInventory = backingInventory;
		this.backingInventory.addListener(this.backingListener);
		return this.synchronizeFromBacking();
	}

	public Inventory getBackingInventory() {
		return this.backingInventory;
	}

	/** Refreshes slots changed internally without firing an inventory listener. */
	public @NonNull InventoryMenu synchronizeFromBacking() {
		if (this.backingInventory == null || this.synchronizing) {
			return this;
		}

		this.synchronizing = true;
		try {
			for (int slot = 0; slot < this.inventory.getSize(); slot++) {
				Item item = this.backingInventory.getItem(slot);
				if (!sameItem(this.inventory.getUnclonedItem(slot), item)) {
					this.inventory.setItem(slot, item);
				}
			}
		} finally {
			this.synchronizing = false;
		}
		return this;
	}

	public MenuTransactionHandler getTransactionHandler() {
		return this.transactionHandler;
	}

	public @NonNull InventoryMenu setTransactionHandler(MenuTransactionHandler transactionHandler) {
		this.transactionHandler = transactionHandler;

		return this;
	}

	public MenuCloseHandler getCloseHandler() {
		return this.closeHandler;
	}

	public @NonNull InventoryMenu setCloseHandler(MenuCloseHandler closeHandler) {
		this.closeHandler = closeHandler;

		return this;
	}

	/**
	 * Opens this menu, replacing the player's current non-permanent window when
	 * needed.
	 *
	 * @return {@code false} only when the request cannot be accepted immediately
	 */
	public boolean send(@NonNull Player player) {
		Objects.requireNonNull(player, "Player cannot be null");
		if (!player.isOnline()) {
			return false;
		}

		Optional<Inventory> currentWindow = player.getTopWindow();
		if (currentWindow.isEmpty()) {
			return this.openWindow(player);
		}
		if (currentWindow.get() == this.inventory) {
			return true;
		}

		player.removeWindow(currentWindow.get());
		player.waitForAck(() -> {
			if (player.isOnline() && player.getTopWindow().isEmpty()) {
				this.openWindow(player);
			}
		});

		return true;
	}

	public boolean close(@NonNull Player player) {
		Objects.requireNonNull(player, "Player cannot be null");
		if (player.getWindowId(this.inventory) == -1) {
			return false;
		}

		player.removeWindow(this.inventory);
		return true;
	}

	public void closeAll() {
		for (Player viewer : new ArrayList<>(this.inventory.getViewers())) {
			this.close(viewer);
		}
	}

	public boolean isViewer(@NonNull Player player) {
		return this.inventory.getViewers().contains(Objects.requireNonNull(player, "Player cannot be null"));
	}

	protected @NonNull MenuTransactionDecision onTransaction(@NonNull MenuTransaction transaction) {
		MenuTransactionHandler activeHandler = this.transactionHandler;
		if (activeHandler == null) {
			return transaction.allow();
		}

		return Objects.requireNonNull(activeHandler.onTransaction(transaction.getPlayer(), transaction), "Menu transaction handler cannot return null");
	}

	protected void onClose(@NonNull Player player) {
		MenuCloseHandler activeHandler = this.closeHandler;
		if (activeHandler != null) {
			activeHandler.onClose(player);
		}
	}

	private boolean openWindow(@NonNull Player player) {
		return player.addWindow(this.inventory) >= 0;
	}

	private void dispatchTransaction(FakeInventory inventory, int slot, Item itemClicked, Item itemClickedWith, ItemStackRequestActionEvent event) {
		MenuTransaction transaction = new MenuTransaction(this, event.getPlayer(), slot, itemClicked, itemClickedWith, event);
		MenuTransactionDecision decision = this.onTransaction(transaction);
		if (decision.isCancelled()) {
			event.setCancelled(true);
		}
	}

	private void copyMenuSlot(Inventory ignored, Item oldItem, int slot) {
		if (this.backingInventory == null || this.synchronizing) {
			return;
		}

		this.synchronizing = true;
		try {
			this.backingInventory.setItem(slot, this.inventory.getItem(slot), false);
		} finally {
			this.synchronizing = false;
		}
	}

	private void copyBackingSlot(Inventory ignored, Item oldItem, int slot) {
		if (this.synchronizing) {
			return;
		}

		this.synchronizing = true;
		try {
			this.inventory.setItem(slot, this.backingInventory.getItem(slot));
		} finally {
			this.synchronizing = false;
		}
	}

	private static boolean sameItem(@NonNull Item first, @NonNull Item second) {
		return first.getCount() == second.getCount() && first.equals(second, true, true);
	}
}
