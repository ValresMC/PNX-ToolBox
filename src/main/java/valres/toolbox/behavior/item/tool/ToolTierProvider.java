package valres.toolbox.behavior.item.tool;

import org.jspecify.annotations.NonNull;

/**
 * Exposes a complete tool tier to the generated data-driven item definition.
 */
public interface ToolTierProvider {
    @NonNull ToolTier getToolTier();
}
