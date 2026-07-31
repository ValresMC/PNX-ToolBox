package valres.toolbox.behavior.block.component.type;

public enum PrecipitationBehavior {
    OBSTRUCT_RAIN_ACCUMULATE_SNOW("obstruct_rain_accumulate_snow"),
    OBSTRUCT_RAIN("obstruct_rain"),
    NONE("none");

    final private String value;

    PrecipitationBehavior(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return this.value;
    }
}
