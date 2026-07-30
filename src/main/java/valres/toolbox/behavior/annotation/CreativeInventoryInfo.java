package valres.toolbox.behavior.annotation;

import org.powernukkitx.item.customitem.data.CreativeCategory;
import org.powernukkitx.item.customitem.data.CreativeGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CreativeInventoryInfo {
    CreativeCategory category() default CreativeCategory.ITEMS;

    CreativeGroup group() default CreativeGroup.NONE;

    String customGroup() default "";

    boolean hidden() default false;
}
