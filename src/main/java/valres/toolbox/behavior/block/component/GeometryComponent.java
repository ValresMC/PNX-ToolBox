package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;

import java.util.LinkedHashMap;
import java.util.Map;

final public class GeometryComponent extends BlockComponent {
    final private String identifier;
    final private String culling;
    final private String cullingLayer;
    final private boolean uvLock;
    final private Map<String, Object> boneVisibility = new LinkedHashMap<>();
    final private boolean ignoreGeometryForIsSolid;
    final private boolean needsLegacyTopRotation;
    final private boolean useBlockTypeLightAbsorption;

    public GeometryComponent() {
        this("minecraft:geometry.full_block");
    }

    public GeometryComponent(@NonNull String identifier) {
        this(
            identifier,
            "",
            "minecraft:culling_layer.undefined",
            false,
            true,
            false,
            false
        );
    }

    public GeometryComponent(
        @NonNull String identifier,
        @NonNull String culling,
        @NonNull String cullingLayer,
        boolean uvLock,
        boolean ignoreGeometryForIsSolid,
        boolean needsLegacyTopRotation,
        boolean useBlockTypeLightAbsorption
    ) {
        this.identifier = identifier;
        this.culling = culling;
        this.cullingLayer = cullingLayer;
        this.uvLock = uvLock;
        this.ignoreGeometryForIsSolid = ignoreGeometryForIsSolid;
        this.needsLegacyTopRotation = needsLegacyTopRotation;
        this.useBlockTypeLightAbsorption = useBlockTypeLightAbsorption;
    }

    public static @NonNull GeometryComponent vanilla(
        @NonNull String identifier
    ) {
        return new GeometryComponent(identifier);
    }

    public @NonNull GeometryComponent addBoneVisibility(
        @NonNull String bone,
        boolean visible
    ) {
        this.boneVisibility.put(bone, visible);
        return this;
    }

    public @NonNull GeometryComponent addBoneVisibility(
        @NonNull String bone,
        @NonNull String condition
    ) {
        this.boneVisibility.put(
            bone,
            Map.of(
                "expression", condition,
                "version", 12
            )
        );
        return this;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.GEOMETRY;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "bone_visibility",
            this.boneVisibility.isEmpty()
                ? new CompoundTag()
                : this.boneVisibility,
            "identifier", this.identifier,
            "culling", this.culling,
            "culling_layer", this.cullingLayer,
            "culling_shape", "",
            "uv_lock", this.uvLock,
            "ignoreGeometryForIsSolid", this.ignoreGeometryForIsSolid,
            "needsLegacyTopRotation", this.needsLegacyTopRotation,
            "useBlockTypeLightAbsorption",
            this.useBlockTypeLightAbsorption
        );
    }
}
