package valres.toolbox.behavior.item.components.type;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;

public final class RepairItem implements ItemComponentValue {
	private final List<?> items;
	private final Object repairAmount;

	public RepairItem(@NonNull Collection<?> items, @NonNull Object repairAmount) {
		this.items = List.copyOf(items);
		if (!(repairAmount instanceof String || repairAmount instanceof Integer)) {
			throw new IllegalArgumentException("Repair amount must be a String expression or an Integer");
		}
		this.repairAmount = repairAmount;
	}

	public static @NonNull RepairItem of(@NonNull BlockDescriptor item, @NonNull Object repairAmount) {
		return new RepairItem(List.of(item), repairAmount);
	}

	public static @NonNull RepairItem of(@NonNull String item, @NonNull Object repairAmount) {
		return new RepairItem(List.of(item), repairAmount);
	}

	public static @NonNull RepairItem of(@NonNull Map<String, ?> item, @NonNull Object repairAmount) {
		return new RepairItem(List.of(item), repairAmount);
	}

	@Override public @NonNull Map<String, ?> toMap() {
		return Map.of("items", this.items, "repair_amount", this.repairAmount);
	}
}
