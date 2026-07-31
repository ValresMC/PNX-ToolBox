package valres.toolbox.behavior.item.components;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import valres.toolbox.behavior.item.ItemComponentNames;
import valres.toolbox.behavior.item.components.type.CooldownType;

public final class CooldownComponent extends DataDrivenItemComponent {
	public static final String TYPE_USE = "use";
	public static final String TYPE_ATTACK = "attack";

	private final String category;
	private final float duration;
	private final String type;

	public CooldownComponent(@NonNull String category, float duration) {
		this(category, duration, (String) null);
	}

	public CooldownComponent(@NonNull String category, float duration, @NonNull CooldownType type) {
		this(category, duration, type.toString());
	}

	public CooldownComponent(String category, float duration, String type) {
		this.category = category;
		this.duration = duration;
		this.type = type;
	}

	@Override public @NonNull String getIdentifier() {
		return ItemComponentNames.COOLDOWN;
	}

	@Override public @NonNull CompoundTag toNBT() {
		return ComponentNbtHelper.compound("category", this.category, "duration", this.duration, "type", this.type);
	}
}
