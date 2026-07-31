package valres.toolbox.behavior.block.component.type;

import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public record MaterialInstance(
    @NonNull String texture,
    @Nullable String renderMethod,
    @Nullable String tintMethod,
    @Nullable Number ambientOcclusion,
    @Nullable Boolean faceDimming,
    @Nullable Boolean isotropic,
    @Nullable Boolean alphaMaskedTint,
    @Nullable Boolean packedBools
) implements BlockComponentValue {
    public MaterialInstance(@NonNull String texture) {
        this(texture, null, null, null, null, null, null, null);
    }

    public MaterialInstance(
        @NonNull String texture,
        @NonNull RenderMethod renderMethod
    ) {
        this(
            texture,
            renderMethod.toString(),
            null,
            1f,
            true,
            false,
            null,
            false
        );
    }

    @Override public @NonNull Map<String, ?> toMap() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("texture", this.texture);
        put(values, "render_method", this.renderMethod);
        put(values, "tint_method", this.tintMethod);
        put(values, "ambient_occlusion", this.ambientOcclusion);
        put(values, "face_dimming", this.faceDimming);
        put(values, "isotropic", this.isotropic);
        put(values, "alpha_masked_tint", this.alphaMaskedTint);
        put(values, "packed_bools", this.packedBools);
        return values;
    }

    private static void put(
        Map<String, Object> values,
        String name,
        Object value
    ) {
        if (value != null) {
            values.put(name, value);
        }
    }
}
