package valres.toolbox.behavior.block.component;

import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.ChestObstructionRule;

/** Controls when this block obstructs the opening of a chest below it. */
public final class ChestObstructionComponent extends BlockComponent {
	private final String obstructionRule;

	public ChestObstructionComponent() {
		this(ChestObstructionRule.SHAPE);
	}

	public ChestObstructionComponent(@NonNull ChestObstructionRule obstructionRule) {
		this(Objects.requireNonNull(obstructionRule, "Obstruction rule cannot be null").toString());
	}

	public ChestObstructionComponent(@NonNull String obstructionRule) {
		this.obstructionRule = Objects.requireNonNull(obstructionRule, "Obstruction rule cannot be null");
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.CHEST_OBSTRUCTION;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.compound("obstruction_rule", this.obstructionRule);
	}
}
