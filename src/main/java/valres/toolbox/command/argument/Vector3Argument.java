package valres.toolbox.command.argument;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.math.Vector3;

public final class Vector3Argument extends Argument<Vector3> {
	public Vector3Argument(String name) {
		super(name);
	}

	public Vector3Argument(String name, boolean optional) {
		super(name, optional);
	}

	@Override public int getMinimumTokens() {
		return 3;
	}

	@Override protected Vector3 parseValue(CommandSender sender, String value) {
		return CoordinateParser.parse(sender, this.getName(), value);
	}

	@Override public String getTypeName() {
		return "x y z";
	}

	@Override public CommandParameter toCommandParameter() {
		return CommandParameter.newType(this.getName(), this.isOptional(), CommandParamType.POSITION_FLOAT);
	}
}
