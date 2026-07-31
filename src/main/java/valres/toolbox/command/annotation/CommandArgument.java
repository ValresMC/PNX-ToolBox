package valres.toolbox.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CommandArguments.class)
public @interface CommandArgument {
	String NO_DEFAULT = "\u0000";

	int order();

	String name();

	CommandArgumentType type() default CommandArgumentType.STRING;

	boolean optional() default false;

	String defaultValue() default NO_DEFAULT;

	String[] values() default {};
}
