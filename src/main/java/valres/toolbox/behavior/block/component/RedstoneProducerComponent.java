package valres.toolbox.behavior.block.component;

import org.jspecify.annotations.NonNull;
import org.powernukkitx.nbt.tag.Tag;
import valres.toolbox.behavior.block.BlockComponentNames;
import valres.toolbox.behavior.block.component.type.BlockFace;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Defines the redstone power emitted by the block and its connected faces.
 */
final public class RedstoneProducerComponent extends BlockComponent {
    final private int power;
    final private String stronglyPoweredFace;
    final private List<String> connectedFaces;
    final private Boolean transformRelative;

    public RedstoneProducerComponent(
        int power,
        @NonNull BlockFace stronglyPoweredFace,
        @NonNull BlockFace... connectedFaces
    ) {
        this(
            power,
            Objects.requireNonNull(
                stronglyPoweredFace,
                "Strongly powered face cannot be null"
            ).toString(),
            Arrays.stream(Objects.requireNonNull(
                connectedFaces,
                "Connected faces cannot be null"
            )).map(BlockFace::toString).toList(),
            null
        );
    }

    public RedstoneProducerComponent(
        int power,
        @NonNull BlockFace stronglyPoweredFace,
        @NonNull List<BlockFace> connectedFaces,
        @Nullable Boolean transformRelative
    ) {
        this(
            power,
            Objects.requireNonNull(
                stronglyPoweredFace,
                "Strongly powered face cannot be null"
            ).toString(),
            Objects.requireNonNull(
                connectedFaces,
                "Connected faces cannot be null"
            ).stream().map(BlockFace::toString).toList(),
            transformRelative
        );
    }

    public RedstoneProducerComponent(
        int power,
        @NonNull String stronglyPoweredFace,
        @NonNull String... connectedFaces
    ) {
        this(
            power,
            stronglyPoweredFace,
            List.copyOf(Arrays.asList(Objects.requireNonNull(
                connectedFaces,
                "Connected faces cannot be null"
            ))),
            null
        );
    }

    public RedstoneProducerComponent(
        int power,
        @NonNull String stronglyPoweredFace,
        @NonNull List<String> connectedFaces,
        @Nullable Boolean transformRelative
    ) {
        this.power = power;
        this.stronglyPoweredFace = Objects.requireNonNull(
            stronglyPoweredFace,
            "Strongly powered face cannot be null"
        );
        this.connectedFaces = List.copyOf(Objects.requireNonNull(
            connectedFaces,
            "Connected faces cannot be null"
        ));
        this.transformRelative = transformRelative;
    }

    @Override public @NonNull String getIdentifier() {
        return BlockComponentNames.REDSTONE_PRODUCER;
    }

    @Override public @NonNull Tag toNBT() {
        return ComponentNbtHelper.compound(
            "power", this.power,
            "strongly_powered_face", this.stronglyPoweredFace,
            "connected_faces",
            this.connectedFaces.isEmpty() ? null : this.connectedFaces,
            "transform_relative", this.transformRelative
        );
    }
}
