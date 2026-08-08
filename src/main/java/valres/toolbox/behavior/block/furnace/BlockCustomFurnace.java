package valres.toolbox.behavior.block.furnace;

import org.powernukkitx.Player;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockLitFurnace;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntityFurnace;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.nbt.tag.StringTag;
import valres.toolbox.behavior.block.ToolBoxBlock;

public abstract class BlockCustomFurnace extends BlockLitFurnace implements ToolBoxBlock {
	protected BlockCustomFurnace(BlockState blockState) {
		super(blockState);
	}

	protected static @NonNull BlockProperties createFurnaceProperties(@NonNull String identifier) {
		return new BlockProperties(identifier, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION, CommonBlockProperties.LIT);
	}

	@Override public final @NonNull Class<? extends BlockEntityFurnace> getBlockEntityClass() {
		return BlockEntityCustomFurnace.class;
	}

	@Override public final @NonNull String getBlockEntityType() {
		return BlockEntityCustomFurnace.TYPE;
	}

	public final boolean isLit() {
		return this.getPropertyValue(CommonBlockProperties.LIT);
	}

	public final @NonNull BlockCustomFurnace setLit(boolean lit) {
		this.setPropertyValue(CommonBlockProperties.LIT, lit);
		return this;
	}

	public int getCookingSpeedMultiplier() {
		return 1;
	}

	public @NonNull String getInventoryTitle() {
		return this.getName();
	}

	public int getBurningLightLevel() {
		return 13;
	}

	public boolean displaysBurningParticles() {
		return true;
	}

	@Override public final int getLightLevel() {
		return this.isLit() ? this.getBurningLightLevel() : 0;
	}

	public @NonNull String getTexturePrefix() {
		String identifier = this.getId();
		int separator = identifier.indexOf(':');
		return separator < 0 ? identifier : identifier.substring(separator + 1);
	}

	public @NonNull String getSideTexture() {
		return this.getTexturePrefix() + "_side";
	}

	public @NonNull String getTopTexture() {
		return this.getTexturePrefix() + "_top";
	}

	public @NonNull String getBottomTexture() {
		return this.getTopTexture();
	}

	public @NonNull String getFrontTexture(boolean lit) {
		return this.getTexturePrefix() + (lit ? "_front_on" : "_front_off");
	}

	@Override public @NonNull Item toItem() {
		Block itemBlock = this.clone();
		itemBlock.setPropertyValues(this.getProperties().getDefaultState().getBlockPropertyValues());
		return new ItemBlock(itemBlock);
	}

	@Override public boolean onActivate(@NonNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
		if (player == null) {
			return false;
		}

		Item itemInHand = player.getInventory().getItemInMainHand();
		if (player.isSneaking() && !(itemInHand.isTool() || itemInHand.isNull())) {
			return false;
		}

		BlockEntityFurnace blockEntity = this.getOrCreateBlockEntity();
		if (!(blockEntity instanceof BlockEntityCustomFurnace furnace)) {
			return false;
		}
		if (furnace.getNbt().contains("Lock") && furnace.getNbt().get("Lock") instanceof StringTag && !furnace.getNbt().getString("Lock").equals(item.getCustomName())) {
			return false;
		}

		return furnace.openInventory(player);
	}
}
