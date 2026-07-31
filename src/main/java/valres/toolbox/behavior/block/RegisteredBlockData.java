package valres.toolbox.behavior.block;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.customblock.CustomBlockDefinition;
import valres.toolbox.behavior.block.builder.BlockBuilder;

public record RegisteredBlockData(
    Block block,
    Class<? extends Block> blockClass,
    BlockBuilder builder,
    CustomBlockDefinition definition
) {
}
