package valres.toolbox.command.argument;

import java.util.Arrays;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.math.Vector3;
import valres.toolbox.command.CommandMessages;
import valres.toolbox.command.exception.ArgumentParseException;

final class CoordinateParser {
	private CoordinateParser() {
	}

	public static Vector3 parse(CommandSender sender, String argumentName, String value) {
		String[] coordinates = value.split(" ", -1);
		if (coordinates.length != 3) {
			throw new ArgumentParseException(argumentName, value, CommandMessages.format(CommandMessages.ARGUMENT_COORDINATES_COUNT, "argument", argumentName));
		}

		boolean hasRelativeCoordinate = Arrays.stream(coordinates).anyMatch(coordinate -> coordinate.startsWith("~"));
		Vector3 origin = hasRelativeCoordinate ? sender.getPosition() : new Vector3();
		return new Vector3(parseCoordinate(sender, argumentName, coordinates[0], origin.getX()), parseCoordinate(sender, argumentName, coordinates[1], origin.getY()), parseCoordinate(sender, argumentName, coordinates[2], origin.getZ()));
	}

	private static double parseCoordinate(CommandSender sender, String argumentName, String coordinate, double origin) {
		boolean relative = coordinate.startsWith("~");
		if (relative && !sender.isEntity()) {
			throw new ArgumentParseException(argumentName, coordinate, CommandMessages.get(CommandMessages.ARGUMENT_RELATIVE_COORDINATES));
		}
		double offset = getOffset(argumentName, coordinate, relative);

		double parsed = relative ? origin + offset : offset;
		if (!Double.isFinite(parsed)) {
			throw new ArgumentParseException(argumentName, coordinate, CommandMessages.format(CommandMessages.ARGUMENT_COORDINATE_RANGE, "coordinate", coordinate));
		}

		return parsed;
	}

	private static double getOffset(String argumentName, String coordinate, boolean relative) {
		if (coordinate.startsWith("^")) {
			throw new ArgumentParseException(argumentName, coordinate, CommandMessages.get(CommandMessages.ARGUMENT_LOCAL_COORDINATES));
		}

		String numericPart = relative ? coordinate.substring(1) : coordinate;
		double offset = 0;
		if (!numericPart.isEmpty()) {
			try {
				offset = Double.parseDouble(numericPart);
			} catch (NumberFormatException exception) {
				throw new ArgumentParseException(argumentName, coordinate, CommandMessages.format(CommandMessages.ARGUMENT_INVALID_COORDINATE, "coordinate", coordinate), exception);
			}
		} else if (!relative) {
			throw new ArgumentParseException(argumentName, coordinate, CommandMessages.format(CommandMessages.ARGUMENT_INVALID_COORDINATE, "coordinate", coordinate));
		}
		return offset;
	}
}
