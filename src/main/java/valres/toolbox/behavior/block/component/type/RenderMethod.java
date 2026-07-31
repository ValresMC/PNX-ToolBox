package valres.toolbox.behavior.block.component.type;

public enum RenderMethod {
	ALPHA_TEST("alpha_test"), ALPHA_TEST_SINGLE_SIDED("alpha_test_single_sided"), ALPHA_TEST_TO_OPAQUE("alpha_test_to_opaque"), ALPHA_TEST_SINGLE_SIDED_TO_OPAQUE("alpha_test_single_sided_to_opaque"), BLEND("blend"), BLEND_SINGLE_SIDED("blend_single_sided"), BLEND_TO_OPAQUE("blend_to_opaque"), DOUBLE_SIDED("double_sided"), OPAQUE("opaque");

	private final String value;

	RenderMethod(String value) {
		this.value = value;
	}

	@Override public String toString() {
		return this.value;
	}
}
