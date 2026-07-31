package valres.toolbox.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import org.jspecify.annotations.NonNull;
import org.powernukkitx.command.CommandSender;
import valres.toolbox.command.annotation.CommandArgument;
import valres.toolbox.command.argument.Argument;
import valres.toolbox.command.argument.Arguments;
import valres.toolbox.command.exception.ArgumentParseException;
import valres.toolbox.command.exception.CommandConfigurationException;
import valres.toolbox.command.exception.CommandInputException;
import valres.toolbox.command.result.CommandFailureReason;
import valres.toolbox.command.rules.Rule;
import valres.toolbox.command.rules.RuleResult;

final class CommandNodeData {
	private static final Pattern VALID_NAME = Pattern.compile("^[a-z0-9_-]+$");

	private String name;
	private String description;
	private List<String> aliases;
	private final List<Argument<?>> arguments = new ArrayList<>();
	private final List<Rule> rules = new ArrayList<>();
	private final List<SubCommand> subCommands = new ArrayList<>();
	private final Map<String, SubCommand> subCommandLookup = new LinkedHashMap<>();

	public CommandNodeData(String name, String description, String[] aliases) {
		this.updateMetadata(name, description, aliases);
	}

	public void updateMetadata(String name, @NonNull String description, @NonNull String[] aliases) {
		this.name = normalizeName(name, "Command name");
		this.description = description;

		Set<String> normalizedAliases = new LinkedHashSet<>();
		for (String alias : aliases) {
			String normalizedAlias = normalizeName(alias, "Command alias");
			if (normalizedAlias.equals(this.name) || !normalizedAliases.add(normalizedAlias)) {
				throw new CommandConfigurationException("Duplicate command alias '" + alias + "'");
			}
		}
		this.aliases = List.copyOf(normalizedAliases);
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public List<String> getAliases() {
		return this.aliases;
	}

	public List<Argument<?>> getArguments() {
		return List.copyOf(this.arguments);
	}

	public List<Rule> getRules() {
		return List.copyOf(this.rules);
	}

	public List<SubCommand> getSubCommands() {
		return List.copyOf(this.subCommands);
	}

	public void addArgument(@NonNull Argument<?> argument) {
		if (this.getArgument(argument.getName()) != null) {
			throw new CommandConfigurationException("Duplicate argument '" + argument.getName() + "' on command node '" + this.name + "'");
		}

		if (!this.arguments.isEmpty()) {
			Argument<?> previous = this.arguments.getLast();
			if (previous.isOptional() && !argument.isOptional()) {
				throw new CommandConfigurationException("Required argument '" + argument.getName() + "' cannot follow optional argument '" + previous.getName() + "'");
			}
			if (previous.getMaximumTokens() == Integer.MAX_VALUE) {
				throw new CommandConfigurationException("Greedy argument '" + previous.getName() + "' must be the last argument");
			}
		}

		this.arguments.add(argument);
	}

	public Argument<?> getArgument(String name) {
		for (Argument<?> argument : this.arguments) {
			if (argument.getName().equalsIgnoreCase(name)) {
				return argument;
			}
		}

		return null;
	}

	public void addRule(@NonNull Rule rule) {
		this.rules.add(Objects.requireNonNull(rule, "Command rule cannot be null"));
	}

	public void addSubCommand(@NonNull SubCommand subCommand) {
		List<String> labels = new ArrayList<>();
		labels.add(subCommand.getName());
		labels.addAll(subCommand.getAliases());
		for (String label : labels) {
			String normalized = label.toLowerCase(Locale.ROOT);
			if (this.subCommandLookup.containsKey(normalized)) {
				throw new CommandConfigurationException("Duplicate sub-command name or alias '" + label + "' below '" + this.name + "'");
			}
		}

		this.subCommands.add(subCommand);
		for (String label : labels) {
			this.subCommandLookup.put(label.toLowerCase(Locale.ROOT), subCommand);
		}
	}

	public SubCommand findSubCommand(String label) {
		return this.subCommandLookup.get(label.toLowerCase(Locale.ROOT));
	}

	public RuleResult testRules(CommandSender sender) {
		List<Rule> failed = new ArrayList<>();
		for (Rule rule : this.rules) {
			if (rule.canExecute(sender)) {
				rule.onPassed(sender);
				continue;
			}

			rule.fail(sender);
			failed.add(rule);
		}

		return new RuleResult(failed);
	}

	public void notifyExecuted(CommandSender sender) {
		this.rules.forEach(rule -> rule.onExecuted(sender));
	}

	public ArgumentsList parse(CommandSender sender, List<String> rawArguments) {
		LinkedHashMap<String, Object> parsed = new LinkedHashMap<>();
		int cursor = 0;

		for (int index = 0; index < this.arguments.size(); index++) {
			Argument<?> argument = this.arguments.get(index);
			int available = rawArguments.size() - cursor;
			int minimumAfter = this.minimumRequiredTokens(index + 1);

			if (argument.isOptional() && available <= minimumAfter) {
				parsed.put(argument.getName(), argument.hasDefault() ? argument.getDefaultValue() : null);
				continue;
			}

			int required = argument.getMinimumTokens();
			if (available < required + minimumAfter) {
				List<String> missing = this.missingArguments(index, available);
				throw new CommandInputException(CommandFailureReason.MISSING_ARGUMENT, CommandMessages.format(CommandMessages.ARGUMENT_MISSING, "arguments", String.join(", ", missing)), Map.of("missing_arguments", missing));
			}

			int consumable = available - minimumAfter;
			int tokenCount = Math.min(argument.getMaximumTokens(), consumable);
			if (tokenCount < required) {
				throw new CommandInputException(CommandFailureReason.MISSING_ARGUMENT, CommandMessages.format(CommandMessages.ARGUMENT_MISSING_ONE, "argument", argument.getName()), Map.of("missing_arguments", List.of(argument.getName())));
			}

			List<String> tokens = rawArguments.subList(cursor, cursor + tokenCount);
			try {
				parsed.put(argument.getName(), argument.parse(sender, tokens));
			} catch (ArgumentParseException exception) {
				throw new CommandInputException(CommandFailureReason.INVALID_ARGUMENT, exception.getMessage(), Map.of("argument", exception.getArgument(), "value", exception.getValue()));
			}
			cursor += tokenCount;
		}

		if (cursor < rawArguments.size()) {
			List<String> excess = List.copyOf(rawArguments.subList(cursor, rawArguments.size()));
			throw new CommandInputException(CommandFailureReason.TOO_MANY_ARGUMENTS, CommandMessages.format(CommandMessages.ARGUMENT_TOO_MANY, "arguments", String.join(" ", excess)), Map.of("excess_arguments", excess));
		}

		return new ArgumentsList(sender, parsed);
	}

	public String getUsage(String label) {
		StringBuilder usage = new StringBuilder("/").append(label);
		for (Argument<?> argument : this.arguments) {
			usage.append(' ').append(argument.getUsage());
		}

		return usage.toString();
	}

	public void loadAnnotatedArguments(Class<?> type) {
		CommandArgument[] definitions = type.getAnnotationsByType(CommandArgument.class);
		Arrays.sort(definitions, Comparator.comparingInt(CommandArgument::order));

		Set<Integer> orders = new LinkedHashSet<>();
		for (CommandArgument definition : definitions) {
			if (!orders.add(definition.order())) {
				throw new CommandConfigurationException("Duplicate @CommandArgument order " + definition.order() + " on " + type.getName());
			}
			this.addArgument(Arguments.fromAnnotation(definition));
		}
	}

	private int minimumRequiredTokens(int startIndex) {
		int count = 0;
		for (int index = startIndex; index < this.arguments.size(); index++) {
			Argument<?> argument = this.arguments.get(index);
			if (!argument.isOptional()) {
				count = Math.addExact(count, argument.getMinimumTokens());
			}
		}

		return count;
	}

	private List<String> missingArguments(int startIndex, int availableTokens) {
		List<String> missing = new ArrayList<>();
		int remaining = availableTokens;
		for (int index = startIndex; index < this.arguments.size(); index++) {
			Argument<?> argument = this.arguments.get(index);
			if (argument.isOptional()) {
				continue;
			}
			if (remaining >= argument.getMinimumTokens()) {
				remaining -= argument.getMinimumTokens();
			} else {
				missing.add(argument.getName());
				remaining = 0;
			}
		}

		return missing;
	}

	private static String normalizeName(@NonNull String name, String field) {
		String normalized = name.toLowerCase(Locale.ROOT);
		if (!VALID_NAME.matcher(normalized).matches()) {
			throw new CommandConfigurationException(field + " '" + name + "' must contain only letters, numbers, underscores or hyphens");
		}

		return normalized;
	}
}
