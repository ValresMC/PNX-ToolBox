package valres.toolbox.behavior.block.component;

import java.util.Objects;
import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.MovableType;

/** Defines how pistons can move the block and whether it is sticky. */
public final class MovableComponent extends BlockComponent {
	private final String movementType;
	private final String sticky;

	public MovableComponent() {
		this(MovableType.PUSH_PULL, null);
	}

	public MovableComponent(@NonNull MovableType movementType, @Nullable String sticky) {
		this(Objects.requireNonNull(movementType, "Movement type cannot be null").toString(), sticky);
	}

	public MovableComponent(@NonNull String movementType, @Nullable String sticky) {
		this.movementType = Objects.requireNonNull(movementType, "Movement type cannot be null");
		this.sticky = sticky;
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.MOVABLE;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.compound("movement_type", this.movementType, "sticky", this.sticky);
	}
}
