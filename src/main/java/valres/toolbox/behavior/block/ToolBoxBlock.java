package valres.toolbox.behavior.block;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.customblock.CustomBlock;
import org.powernukkitx.block.customblock.CustomBlockDefinition;

public interface ToolBoxBlock extends CustomBlock {
	@Override default @NonNull CustomBlockDefinition getDefinition() {
		if (!(this instanceof Block block)) {
			throw new IllegalStateException("ToolBoxBlock must be implemented by a Block");
		}

		return CustomBlockRegistry.getInstance().resolveDefinition(block);
	}
}
