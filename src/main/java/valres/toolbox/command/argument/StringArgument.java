package valres.toolbox.command.argument;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;

public class StringArgument extends Argument<String> {
	public StringArgument(String name) {
		super(name);
	}

	public StringArgument(String name, boolean optional) {
		super(name, optional);
	}

	public StringArgument(String name, String defaultValue) {
		super(name, defaultValue);
	}

	@Override protected String parseValue(CommandSender sender, String value) {
		return value;
	}

	@Override public CommandParameter toCommandParameter() {
		return CommandParameter.newType(this.getName(), this.isOptional(), CommandParamType.ID);
	}
}
