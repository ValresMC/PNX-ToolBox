package valres.toolbox.command.annotation;

import org.powernukkitx.permission.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermission {
    String value() default "";
    String defaultValue() default Permission.DEFAULT_OP;
}
