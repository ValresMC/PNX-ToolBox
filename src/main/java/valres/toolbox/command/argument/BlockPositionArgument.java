package valres.toolbox.command.argument;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;

final public class BlockPositionArgument extends Argument<BlockVector3> {
    public BlockPositionArgument(String name) {
        super(name);
    }

    public BlockPositionArgument(String name, boolean optional) {
        super(name, optional);
    }

    @Override public int getMinimumTokens() {
        return 3;
    }

    @Override protected BlockVector3 parseValue(CommandSender sender, String value) {
        Vector3 vector = CoordinateParser.parse(sender, this.getName(), value);

        return vector.asBlockVector3();
    }

    @Override public String getTypeName() {
        return "block-position";
    }

    @Override public CommandParameter toCommandParameter() {
        return CommandParameter.newType(this.getName(), this.isOptional(), CommandParamType.POSITION);
    }
}
