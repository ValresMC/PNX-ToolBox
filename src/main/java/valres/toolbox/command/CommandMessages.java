package valres.toolbox.command;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.plugin.Plugin;
import org.powernukkitx.utils.Config;
import org.powernukkitx.utils.ConfigSection;
import org.powernukkitx.utils.TextFormat;

public final class CommandMessages {
	public static final String FILE_NAME = "command-messages-config.yml";

	public static final String RULE_PERMISSION = "rules.permission";
	public static final String RULE_ONLY_PLAYER = "rules.only-player";
	public static final String RULE_ONLY_CONSOLE = "rules.only-console";
	public static final String RULE_ONLY_RCON = "rules.only-rcon";
	public static final String RULE_COOLDOWN = "rules.cooldown";
	public static final String RULE_PREDICATE = "rules.predicate";

	public static final String ARGUMENT_MISSING = "arguments.missing";
	public static final String ARGUMENT_MISSING_ONE = "arguments.missing-one";
	public static final String ARGUMENT_TOO_MANY = "arguments.too-many";
	public static final String ARGUMENT_TOKEN_COUNT = "arguments.token-count";
	public static final String ARGUMENT_INTEGER = "arguments.integer";
	public static final String ARGUMENT_INTEGER_RANGE = "arguments.integer-range";
	public static final String ARGUMENT_LONG = "arguments.long";
	public static final String ARGUMENT_NUMBER = "arguments.number";
	public static final String ARGUMENT_NUMBER_RANGE = "arguments.number-range";
	public static final String ARGUMENT_OPTION = "arguments.option";
	public static final String ARGUMENT_DYNAMIC_ENUM = "arguments.dynamic-enum";
	public static final String ARGUMENT_PLAYER_NOT_ONLINE = "arguments.player-not-online";
	public static final String ARGUMENT_INVALID_SELECTOR = "arguments.invalid-selector";
	public static final String ARGUMENT_COORDINATES_COUNT = "arguments.coordinates-count";
	public static final String ARGUMENT_RELATIVE_COORDINATES = "arguments.relative-coordinates";
	public static final String ARGUMENT_LOCAL_COORDINATES = "arguments.local-coordinates";
	public static final String ARGUMENT_INVALID_COORDINATE = "arguments.invalid-coordinate";
	public static final String ARGUMENT_COORDINATE_RANGE = "arguments.coordinate-range";
	public static final String ARGUMENT_LEVEL_NOT_LOADED = "arguments.level-not-loaded";
	public static final String ARGUMENT_UUID = "arguments.uuid";

	public static final String COMMAND_RULE_FAILED = "command.rule-failed";
	public static final String COMMAND_EXECUTION_ERROR = "command.execution-error";
	public static final String COMMAND_USAGE = "command.usage";

	private static final Map<String, String> DEFAULTS = Map.ofEntries(
	    Map.entry(RULE_PERMISSION, "&cYou do not have permission to use this command. (&7{permission}&c)"), Map.entry(RULE_ONLY_PLAYER, "&cThis command can only be used in-game."), Map.entry(RULE_ONLY_CONSOLE, "&cThis command can only be used from the local console."), Map.entry(RULE_ONLY_RCON, "&cThis command can only be used through RCON."), Map.entry(RULE_COOLDOWN, "&cYou must wait {time}s before using this command again."), Map.entry(RULE_PREDICATE, "&cYou cannot use this command right now."), Map.entry(ARGUMENT_MISSING, "&cMissing required argument(s): {arguments}"), Map.entry(ARGUMENT_MISSING_ONE, "&cMissing required argument: {argument}"), Map.entry(ARGUMENT_TOO_MANY, "&cToo many arguments: {arguments}"), Map.entry(ARGUMENT_TOKEN_COUNT, "&cArgument '{argument}' expects {count} token(s)."), Map.entry(ARGUMENT_INTEGER, "&cArgument '{argument}' must be an integer."), Map.entry(ARGUMENT_INTEGER_RANGE, "&cArgument '{argument}' must be between {minimum} and {maximum}."),
	    Map.entry(ARGUMENT_LONG, "&cArgument '{argument}' must be a long integer."), Map.entry(ARGUMENT_NUMBER, "&cArgument '{argument}' must be a number."), Map.entry(ARGUMENT_NUMBER_RANGE, "&cArgument '{argument}' must be between {minimum} and {maximum}."), Map.entry(ARGUMENT_OPTION, "&cArgument '{argument}' must be one of: {options}."), Map.entry(ARGUMENT_DYNAMIC_ENUM, "&cUnknown value '{value}' for argument '{argument}'."), Map.entry(ARGUMENT_PLAYER_NOT_ONLINE, "&cPlayer '{player}' is not online."), Map.entry(ARGUMENT_INVALID_SELECTOR, "&cInvalid entity selector '{selector}'."), Map.entry(ARGUMENT_COORDINATES_COUNT, "&cArgument '{argument}' requires three coordinates."), Map.entry(ARGUMENT_RELATIVE_COORDINATES, "&cRelative coordinates require an in-game entity sender."), Map.entry(ARGUMENT_LOCAL_COORDINATES, "&cLocal coordinates using '^' are not supported."), Map.entry(ARGUMENT_INVALID_COORDINATE, "&cInvalid coordinate '{coordinate}'."),
	    Map.entry(ARGUMENT_COORDINATE_RANGE, "&cCoordinate '{coordinate}' is outside the supported range."), Map.entry(ARGUMENT_LEVEL_NOT_LOADED, "&cLevel '{level}' is not loaded."), Map.entry(ARGUMENT_UUID, "&cArgument '{argument}' must be a valid UUID."), Map.entry(COMMAND_RULE_FAILED, "&cCommand rules were not satisfied."), Map.entry(COMMAND_EXECUTION_ERROR, "&cAn error occurred while executing this command."), Map.entry(COMMAND_USAGE, "&eUsage: {usage}")
	);

	private static volatile Config config;
	private static volatile Map<String, String> cache = buildCache(null);

	private CommandMessages() {
	}

	public static synchronized void load(@NonNull Plugin plugin) {
		plugin.saveResource(FILE_NAME);
		config = new Config(plugin.getDataFolder() + "/" + FILE_NAME);
		cache = buildCache(config);
	}

	public static synchronized void use(@NonNull Config commandMessagesConfig) {
		config = Objects.requireNonNull(commandMessagesConfig, "Command messages config cannot be null");
		cache = buildCache(config);
	}

	public static synchronized void reload() {
		if (config != null) {
			config.reload();
		}
		cache = buildCache(config);
	}

	public static synchronized void reset() {
		config = null;
		cache = buildCache(null);
	}

	public static String get(@NonNull String key) {
		String message = cache.get(key);

		return message == null ? colorize(key) : message;
	}

	public static String format(String key, Map<String, ?> placeholders) {
		return replacePlaceholders(get(key), placeholders);
	}

	public static String format(String key, String placeholder, Object value) {
		return format(key, Map.of(placeholder, value));
	}

	public static String formatRaw(@NonNull String message, Map<String, ?> placeholders) {
		return colorize(replacePlaceholders(message, placeholders));
	}

	public static String formatRaw(String message) {
		return formatRaw(message, Map.of());
	}

	public static void send(@NonNull CommandSender sender, String key, Map<String, ?> placeholders) {
		String message = format(key, placeholders);
		if (!message.isEmpty()) {
			sender.sendMessage(message);
		}
	}

	public static void send(CommandSender sender, String key) {
		send(sender, key, Map.of());
	}

	public static void sendRaw(@NonNull CommandSender sender, String message, Map<String, ?> placeholders) {
		String formatted = formatRaw(message, placeholders);
		if (!formatted.isEmpty()) {
			sender.sendMessage(formatted);
		}
	}

	private static String resolve(Config source, String path) {
		if (source == null) {
			return null;
		}

		String[] parts = path.split("\\.");
		Object current = source.get(parts[0]);
		for (int index = 1; index < parts.length && current != null; index++) {
			if (current instanceof ConfigSection section) {
				current = section.get(parts[index]);
			} else if (current instanceof Map<?, ?> map) {
				current = map.get(parts[index]);
			} else {
				return null;
			}
		}

		return current instanceof String value ? value : null;
	}

	private static Map<String, String> buildCache(Config source) {
		Map<String, String> messages = new LinkedHashMap<>();
		DEFAULTS.forEach((key, fallback) -> {
			String configured = resolve(source, key);
			messages.put(key, colorize(configured == null ? fallback : configured));
		});

		return Map.copyOf(messages);
	}

	private static String replacePlaceholders(@NonNull String message, @NonNull Map<String, ?> placeholders) {
		String formatted = message;
		for (Map.Entry<String, ?> entry : placeholders.entrySet()) {
			formatted = formatted.replace("{" + entry.getKey() + "}", Objects.toString(entry.getValue(), ""));
		}

		return formatted;
	}

	private static String colorize(String message) {
		return TextFormat.colorize('&', message);
	}
}
