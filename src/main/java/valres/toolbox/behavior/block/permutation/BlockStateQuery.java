package valres.toolbox.behavior.block.permutation;

import org.jspecify.annotations.NonNull;

public final class BlockStateQuery {
	private BlockStateQuery() {
	}

	public static @NonNull String equals(@NonNull String stateName, Object value) {
		return state(stateName) + " == " + value(value);
	}

	public static @NonNull String notEquals(@NonNull String stateName, Object value) {
		return state(stateName) + " != " + value(value);
	}

	public static @NonNull String state(@NonNull String stateName) {
		return "q.block_state('" + escape(stateName) + "')";
	}

	private static @NonNull String value(Object value) {
		if (value instanceof Boolean bool) {
			return bool ? "true" : "false";
		}
		if (value instanceof Number) {
			return value.toString();
		}
		return "'" + escape(String.valueOf(value)) + "'";
	}

	private static @NonNull String escape(@NonNull String value) {
		return value.replace("\\", "\\\\").replace("'", "\\'");
	}
}
