package valres.toolbox.behavior.block.component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.CardinalDirection;
import valres.toolbox.behavior.block.component.type.ConnectionRuleMode;

/** Defines which neighboring blocks and directions this block connects to. */
public final class ConnectionRuleComponent extends BlockComponent {
	private final String acceptsConnectionsFrom;
	private final List<String> enabledDirections;

	public ConnectionRuleComponent() {
		this(null, List.of());
	}

	public ConnectionRuleComponent(@Nullable ConnectionRuleMode acceptsConnectionsFrom, @NonNull CardinalDirection... enabledDirections) {
		this(acceptsConnectionsFrom == null ? null : acceptsConnectionsFrom.toString(), Arrays.stream(Objects.requireNonNull(enabledDirections, "Enabled directions cannot be null")).map(CardinalDirection::toString).toList());
	}

	public ConnectionRuleComponent(@Nullable String acceptsConnectionsFrom, @NonNull String... enabledDirections) {
		this(acceptsConnectionsFrom, List.copyOf(Arrays.asList(Objects.requireNonNull(enabledDirections, "Enabled directions cannot be null"))));
	}

	private ConnectionRuleComponent(@Nullable String acceptsConnectionsFrom, @NonNull List<String> enabledDirections) {
		this.acceptsConnectionsFrom = acceptsConnectionsFrom;
		this.enabledDirections = List.copyOf(Objects.requireNonNull(enabledDirections, "Enabled directions cannot be null"));
	}

	@Override public @NonNull String getIdentifier() {
		return BlockComponentNames.CONNECTION_RULE;
	}

	@Override public @NonNull Tag toNBT() {
		return ComponentNbtHelper.compound("accepts_connections_from", this.acceptsConnectionsFrom, "enabled_directions", this.enabledDirections.isEmpty() ? null : this.enabledDirections);
	}
}
