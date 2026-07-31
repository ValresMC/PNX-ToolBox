package valres.toolbox.behavior.block.permutation.resolver;

import java.util.List;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.block.BlockCrops;
import org.powernukkitx.block.BlockNetherWart;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.type.IntPropertyType;
import valres.toolbox.behavior.block.builder.BlockBuilder;
import valres.toolbox.behavior.block.component.CollisionBoxComponent;
import valres.toolbox.behavior.block.component.GeometryComponent;
import valres.toolbox.behavior.block.component.MaterialInstancesComponent;
import valres.toolbox.behavior.block.component.SelectionBoxComponent;
import valres.toolbox.behavior.block.component.type.MaterialInstance;
import valres.toolbox.behavior.block.component.type.RenderMethod;
import valres.toolbox.behavior.block.permutation.BlockPermutation;
import valres.toolbox.behavior.block.permutation.BlockStateQuery;
import valres.toolbox.behavior.block.permutation.PermutationsResolver;

public final class CropsPermutationResolver extends PermutationsResolver {
	@Override public boolean supports(@NonNull BlockBuilder builder) {
		return builder.getBlock() instanceof BlockCrops || builder.getBlock() instanceof BlockNetherWart;
	}

	@Override public void resolve(@NonNull BlockBuilder builder) {
		IntPropertyType growthProperty;
		int maximumGrowth;

		if (builder.getBlock() instanceof BlockCrops crops) {
			growthProperty = CommonBlockProperties.GROWTH;
			maximumGrowth = crops.getMaxGrowth();
		} else if (builder.getBlock() instanceof BlockNetherWart) {
			growthProperty = CommonBlockProperties.AGE_4;
			maximumGrowth = CommonBlockProperties.AGE_4.getMax();
		} else {
			return;
		}

		if (!builder.getProperties().containProperty(growthProperty)) {
			throw new IllegalStateException("Crop block '" + builder.getIdentifier() + "' must declare the '" + growthProperty.getName() + "' property");
		}

		builder.addComponent(new GeometryComponent("geometry.crop"));
		builder.addComponent(new CollisionBoxComponent(false));
		builder.addTag("minecraft:crop");

		for (int growth = 0; growth <= maximumGrowth; growth++) {
			float height = maximumGrowth == 0 ? 11.2f : ((growth + 1f) / maximumGrowth) * 0.7f * 16f;

			BlockPermutation permutation = new BlockPermutation(BlockStateQuery.equals(growthProperty.getName(), growth));
			permutation.addComponent(MaterialInstancesComponent.all(new MaterialInstance(builder.getName() + "_" + growth, RenderMethod.ALPHA_TEST)));
			permutation.addComponent(SelectionBoxComponent.box(List.of(-8f, 0f, -8f), List.of(16f, height, 16f)));
			builder.addPermutation(permutation);
		}
	}
}
