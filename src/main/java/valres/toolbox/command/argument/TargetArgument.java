package valres.toolbox.command.argument;

import java.util.List;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.selector.EntitySelectorAPI;
import org.powernukkitx.entity.Entity;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;

public final class TargetArgument extends Argument<List<Entity>> {
	public TargetArgument(String name) {
		super(name);
	}

	public TargetArgument(String name, boolean optional) {
		super(name, optional);
	}

	@Override protected List<Entity> parseValue(CommandSender sender, String value) {
		if (!value.startsWith("@")) {
			Player player = sender.getServer().getPlayerExact(value);
			if (player == null) {
				throw new ArgumentParseException(this.getName(), value, CommandMessages.format(CommandMessages.ARGUMENT_PLAYER_NOT_ONLINE, "player", value));
			}

			return List.of(player);
		}

		try {
			return List.copyOf(EntitySelectorAPI.getAPI().matchEntities(sender, value));
		} catch (Exception exception) {
			throw new ArgumentParseException(this.getName(), value, CommandMessages.format(CommandMessages.ARGUMENT_INVALID_SELECTOR, "selector", value), exception);
		}
	}

	@Override public String getTypeName() {
		return "target";
	}

	@Override public CommandParameter toCommandParameter() {
		return CommandParameter.newType(this.getName(), this.isOptional(), CommandParamType.SELECTION);
	}
}
