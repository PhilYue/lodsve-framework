package lodsve.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 鉴权:需要授权(authorization).
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0
 * @createTime 2014-12-7 14:07
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authz {
    /**
     * 所需角色
     *
     * @return
     */
    String[] value() default "";
}
