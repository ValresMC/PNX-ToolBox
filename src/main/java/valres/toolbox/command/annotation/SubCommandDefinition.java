package valres.toolbox.command.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines toolbox sub-command metadata. Root commands use PNX's native
 * {@code org.powernukkitx.plugin.annotation.CommandDefinition} instead.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommandDefinition {
    String name();
    String description() default "";
    String[] aliases() default {};
}
