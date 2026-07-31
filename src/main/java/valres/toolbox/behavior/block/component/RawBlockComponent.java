package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;

public final class RawBlockComponent extends BlockComponent {
	private final String identifier;
	private final Tag value;

	public RawBlockComponent(@NonNull String identifier, @NonNull Tag value) {
		this.identifier = identifier;
		this.value = value;
	}

	@Override public @NonNull String getIdentifier() {
		return this.identifier;
	}

	@Override public @NonNull Tag toNBT() {
		return this.value;
	}
}
