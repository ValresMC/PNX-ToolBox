package valres.toolbox.command.argument;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.command.data.CommandParameter;

public final class TextArgument extends StringArgument {
	public TextArgument(String name) {
		super(name);
	}

	public TextArgument(String name, boolean optional) {
		super(name, optional);
	}

	public TextArgument(String name, String defaultValue) {
		super(name, defaultValue);
	}

	@Override public int getMaximumTokens() {
		return Integer.MAX_VALUE;
	}

	@Override public String getTypeName() {
		return "text";
	}

	@Override public CommandParameter toCommandParameter() {
		return CommandParameter.newType(this.getName(), this.isOptional(), CommandParamType.MESSAGE);
	}
}
