package valres.toolbox.behavior.block.component;

import java.util.List;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.BlockBox;

public final class SelectionBoxComponent extends BlockComponent {
	private final boolean enabled;
	private final BlockBox box;

	public SelectionBoxComponent(boolean enabled) {
		this(enabled, BlockBox.cube());
	}

	public SelectionBoxComponent(@NonNull BlockBox box) {
		this(true, box);
	}

	private SelectionBoxComponent(boolean enabled, @NonNull BlockBox box) {
		this.enabled = enabled;
		this.box = box;
	}

	public static @NonNull SelectionBoxComponent box(@NonNull List<? extends Number> origin, @NonNull List<? extends Number> size) {
		return new SelectionBoxComponent(new BlockBox(origin, size));
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.SELECTION_BOX;
	}

	@Override public @NonNull Tag toNBT() {
		if (!this.enabled) {
			return ComponentNbtHelper.tag(false);
		}

		return ComponentNbtHelper.compound("enabled", true, "origin", this.box.origin(), "size", this.box.size());
	}
}
