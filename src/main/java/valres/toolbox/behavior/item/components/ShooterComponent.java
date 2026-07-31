package valres.toolbox.behavior.item.components;

import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;
import valres.toolbox.behavior.item.components.type.Ammunition;

public final class ShooterComponent extends DataDrivenItemComponent {
	private final List<?> ammunition;
	private final Boolean chargeOnDraw;
	private final Float maxDrawDuration;
	private final Boolean scalePowerByDrawDuration;

	public ShooterComponent(@NonNull Collection<?> ammunition) {
		this(ammunition, null, null, null);
	}

	public ShooterComponent(@NonNull Collection<?> ammunition, Boolean chargeOnDraw, Float maxDrawDuration, Boolean scalePowerByDrawDuration) {
		this.ammunition = List.copyOf(ammunition);
		this.chargeOnDraw = chargeOnDraw;
		this.maxDrawDuration = maxDrawDuration;
		this.scalePowerByDrawDuration = scalePowerByDrawDuration;
	}

	public static @NonNull Ammunition ammunition(@NonNull String item) {
		return new Ammunition(item);
	}

	public static @NonNull Ammunition ammunition(@NonNull String item, Boolean searchInventory, Boolean useInCreative, Boolean useOffhand) {
		return new Ammunition(item, searchInventory, useInCreative, useOffhand);
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.SHOOTER;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("ammunition", this.ammunition, "charge_on_draw", this.chargeOnDraw, "max_draw_duration", this.maxDrawDuration, "scale_power_by_draw_duration", this.scalePowerByDrawDuration);
	}
}
