package valres.toolbox.behavior.block.permutation.resolver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.MinecraftCardinalDirection;
import valres.toolbox.behavior.block.builder.BlockBuilder;
import valres.toolbox.behavior.block.component.*;
import valres.toolbox.behavior.block.component.type.BlockVisual;
import valres.toolbox.behavior.block.component.type.MaterialInstance;
import valres.toolbox.behavior.block.component.type.MaterialInstanceTarget;
import valres.toolbox.behavior.block.component.type.RenderMethod;
import valres.toolbox.behavior.block.furnace.BlockCustomFurnace;
import valres.toolbox.behavior.block.permutation.BlockPermutation;
import valres.toolbox.behavior.block.permutation.BlockStateQuery;
import valres.toolbox.behavior.block.permutation.PermutationsResolver;
import valres.toolbox.behavior.block.trait.PlacementDirectionTrait;

public final class FurnacePermutationResolver extends PermutationsResolver {
	private static final String GEOMETRY = "minecraft:geometry.full_block";

	@Override public boolean supports(@NonNull BlockBuilder builder) {
		return builder.getBlock() instanceof BlockCustomFurnace;
	}

	@Override public void resolve(@NonNull BlockBuilder builder) {
		if (!(builder.getBlock() instanceof BlockCustomFurnace furnace)) {
			return;
		}

		this.validate(builder, furnace);
		builder.addTrait(PlacementDirectionTrait.cardinal(180));
		builder.addComponent(new GeometryComponent(GEOMETRY));
		builder.addComponent(new CustomComponentsComponent(true, true, false));
		builder.addComponent(new MaterialInstancesComponent(this.blockMaterials(furnace, false)));
		builder.addComponent(new TransformationComponent(null, List.of(0, 180, 0), null));
		builder.addComponent(new ItemVisualComponent(new BlockVisual(GEOMETRY, this.itemMaterials(furnace))));

		for (MinecraftCardinalDirection direction : MinecraftCardinalDirection.VALUES) {
			for (boolean lit : List.of(false, true)) {
				String condition = BlockStateQuery.equals(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION.getName(), direction.name().toLowerCase(Locale.ROOT)) + " && " + BlockStateQuery.equals(CommonBlockProperties.LIT.getName(), lit);
				BlockPermutation permutation = new BlockPermutation(condition);
				permutation.addComponent(new MaterialInstancesComponent(this.blockMaterials(furnace, lit)));
				permutation.addComponent(new TransformationComponent(null, List.of(0, this.rotation(direction), 0), null));
				permutation.addComponent(new LightEmissionComponent(lit ? furnace.getBurningLightLevel() : 0));
				builder.addPermutation(permutation);
			}
		}
	}

	private void validate(@NonNull BlockBuilder builder, @NonNull BlockCustomFurnace furnace) {
		if (!builder.getProperties().containProperty(CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION)) {
			throw new IllegalStateException("Furnace block '" + builder.getIdentifier() + "' must declare the '" + CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION.getName() + "' property");
		}
		if (!builder.getProperties().containProperty(CommonBlockProperties.LIT)) {
			throw new IllegalStateException("Furnace block '" + builder.getIdentifier() + "' must declare the '" + CommonBlockProperties.LIT.getName() + "' property");
		}

		int speed = furnace.getCookingSpeedMultiplier();
		if (speed < 1 || speed > 200) {
			throw new IllegalStateException("Furnace block '" + builder.getIdentifier() + "' must use a cooking speed multiplier between 1 and 200");
		}

		int light = furnace.getBurningLightLevel();
		if (light < 0 || light > 15) {
			throw new IllegalStateException("Furnace block '" + builder.getIdentifier() + "' must use a burning light level between 0 and 15");
		}

		this.requireTexture(builder, "side", furnace.getSideTexture());
		this.requireTexture(builder, "top", furnace.getTopTexture());
		this.requireTexture(builder, "bottom", furnace.getBottomTexture());
		this.requireTexture(builder, "unlit front", furnace.getFrontTexture(false));
		this.requireTexture(builder, "lit front", furnace.getFrontTexture(true));
	}

	private void requireTexture(@NonNull BlockBuilder builder, @NonNull String face, @NonNull String texture) {
		if (texture.isBlank()) {
			throw new IllegalStateException("Furnace block '" + builder.getIdentifier() + "' has an empty " + face + " texture");
		}
	}

	private @NonNull Map<String, MaterialInstance> blockMaterials(@NonNull BlockCustomFurnace furnace, boolean lit) {
		Map<String, MaterialInstance> materials = this.sideMaterials(furnace);
		materials.put(MaterialInstanceTarget.NORTH.toString(), this.material(furnace.getFrontTexture(lit)));
		return materials;
	}

	private @NonNull Map<String, MaterialInstance> itemMaterials(@NonNull BlockCustomFurnace furnace) {
		Map<String, MaterialInstance> materials = this.sideMaterials(furnace);
		materials.put(MaterialInstanceTarget.SOUTH.toString(), this.material(furnace.getFrontTexture(false)));
		return materials;
	}

	private @NonNull Map<String, MaterialInstance> sideMaterials(@NonNull BlockCustomFurnace furnace) {
		Map<String, MaterialInstance> materials = new LinkedHashMap<>();
		MaterialInstance side = this.material(furnace.getSideTexture());
		materials.put(MaterialInstanceTarget.UP.toString(), this.material(furnace.getTopTexture()));
		materials.put(MaterialInstanceTarget.DOWN.toString(), this.material(furnace.getBottomTexture()));
		materials.put(MaterialInstanceTarget.NORTH.toString(), side);
		materials.put(MaterialInstanceTarget.SOUTH.toString(), side);
		materials.put(MaterialInstanceTarget.WEST.toString(), side);
		materials.put(MaterialInstanceTarget.EAST.toString(), side);
		return materials;
	}

	private @NonNull MaterialInstance material(@NonNull String texture) {
		return new MaterialInstance(texture, RenderMethod.OPAQUE);
	}

	private int rotation(@NonNull MinecraftCardinalDirection direction) {
		return switch (direction) {
			case NORTH -> 0;
			case EAST -> 270;
			case SOUTH -> 180;
			case WEST -> 90;
		};
	}
}
