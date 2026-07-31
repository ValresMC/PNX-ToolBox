package valres.toolbox.manager.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import valres.toolbox.manager.enums.ManagerPriority;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ManagerPriorityInfo {
	ManagerPriority value();
}
