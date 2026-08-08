package valres.toolbox.menu;

import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.inventory.fake.FakeInventory;
import org.powernukkitx.inventory.fake.FakeInventoryType;

/**
 * Describes a menu backed by one of PowerNukkitX's native fake inventory types.
 */
public final class MenuType {
	public static final MenuType CHEST = fixed(FakeInventoryType.CHEST, 27, "Chest");
	public static final MenuType DOUBLE_CHEST = fixed(FakeInventoryType.DOUBLE_CHEST, 54, "Double Chest");
	public static final MenuType ENDER_CHEST = fixed(FakeInventoryType.ENDER_CHEST, 27, "Ender Chest");
	public static final MenuType FURNACE = fixed(FakeInventoryType.FURNACE, 3, "Furnace");
	public static final MenuType BREWING_STAND = fixed(FakeInventoryType.BREWING_STAND, 5, "Brewing Stand");
	public static final MenuType DISPENSER = fixed(FakeInventoryType.DISPENSER, 9, "Dispenser");
	public static final MenuType DROPPER = fixed(FakeInventoryType.DROPPER, 9, "Dropper");
	public static final MenuType HOPPER = fixed(FakeInventoryType.HOPPER, 5, "Hopper");
	public static final MenuType SHULKER_BOX = fixed(FakeInventoryType.SHULKER_BOX, 27, "Shulker Box");
	public static final MenuType WORKBENCH = fixed(FakeInventoryType.WORKBENCH, 9, "Crafting");

	private final FakeInventoryType nativeType;
	private final int size;
	private final String defaultName;

	private MenuType(@NonNull FakeInventoryType nativeType, int size, @NonNull String defaultName) {
		if (size <= 0) {
			throw new IllegalArgumentException("Menu size must be greater than zero");
		}

		this.nativeType = Objects.requireNonNull(nativeType, "Native inventory type cannot be null");
		this.size = size;
		this.defaultName = Objects.requireNonNull(defaultName, "Default menu name cannot be null");
	}

	/**
	 * Creates an entity-backed container with a custom number of slots.
	 */
	public static @NonNull MenuType entity(int size) {
		return new MenuType(FakeInventoryType.ENTITY, size, "Menu");
	}

	public @NonNull FakeInventoryType getNativeType() {
		return this.nativeType;
	}

	public int getSize() {
		return this.size;
	}

	public @NonNull String getDefaultName() {
		return this.defaultName;
	}

	@NonNull FakeInventory createInventory(@NonNull String name) {
		return new FakeInventory(this.nativeType, name, this.size);
	}

	private static @NonNull MenuType fixed(@NonNull FakeInventoryType nativeType, int size, @NonNull String defaultName) {
		return new MenuType(nativeType, size, defaultName);
	}

	@Override public String toString() {
		return this.nativeType == FakeInventoryType.ENTITY ? "ENTITY(" + this.size + ")" : this.nativeType.name();
	}
}
