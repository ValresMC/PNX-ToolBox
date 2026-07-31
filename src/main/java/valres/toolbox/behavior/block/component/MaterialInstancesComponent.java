package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.MaterialInstance;
import valres.toolbox.behavior.block.component.type.MaterialInstanceTarget;
import valres.toolbox.behavior.block.component.type.RenderMethod;

import java.util.LinkedHashMap;
import java.util.Map;

final public class MaterialInstancesComponent extends BlockComponent {
    final private Map<String, Object> instances;

    public MaterialInstancesComponent(
        @NonNull Map<String, ?> instances
    ) {
        this.instances = new LinkedHashMap<>(instances);
    }

    public static @NonNull MaterialInstancesComponent all(
        @NonNull MaterialInstance instance
    ) {
        return new MaterialInstancesComponent(
            Map.of(MaterialInstanceTarget.ALL.toString(), instance)
        );
    }

    public static @NonNull MaterialInstancesComponent sided(
        @NonNull String side
    ) {
        return sided(side, side, side, RenderMethod.OPAQUE);
    }

    public static @NonNull MaterialInstancesComponent sided(
        @NonNull String side,
        @NonNull String top,
        @NonNull String bottom,
        @NonNull RenderMethod renderMethod
    ) {
        MaterialInstance sideInstance = new MaterialInstance(
            side,
            renderMethod
        );

        return new MaterialInstancesComponent(Map.of(
            MaterialInstanceTarget.NORTH.toString(), sideInstance,
            MaterialInstanceTarget.EAST.toString(), sideInstance,
            MaterialInstanceTarget.SOUTH.toString(), sideInstance,
            MaterialInstanceTarget.WEST.toString(), sideInstance,
            MaterialInstanceTarget.UP.toString(),
            new MaterialInstance(top, renderMethod),
            MaterialInstanceTarget.DOWN.toString(),
            new MaterialInstance(bottom, renderMethod)
        ));
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.MATERIAL_INSTANCES;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "mappings", new CompoundTag(),
            "materials", this.instances
        );
    }
}
