package valres.toolbox.behavior.item.components.type;

import java.util.LinkedHashMap;
import java.util.Map;
import org.jspecify.annotations.NonNull;

public final class DurabilityThreshold implements ItemComponentValue {
	private final int durability;
	private final String particleType;
	private final String soundEvent;

	public DurabilityThreshold(int durability) {
		this(durability, null, null);
	}

	public DurabilityThreshold(int durability, String particleType, String soundEvent) {
		this.durability = durability;
		this.particleType = particleType;
		this.soundEvent = soundEvent;
	}

	@Override public @NonNull Map<String, ?> toMap() {
		Map<String, Object> values = new LinkedHashMap<>();
		values.put("durability", this.durability);
		values.put("particle_type", this.particleType);
		values.put("sound_event", this.soundEvent);
		return values;
	}
}
