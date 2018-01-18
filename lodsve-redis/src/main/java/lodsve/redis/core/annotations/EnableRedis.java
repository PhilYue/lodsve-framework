package lodsve.redis.core.annotations;

import lodsve.core.configuration.EnableLodsve;
import lodsve.redis.core.config.RedisConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用redis.
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version V1.0, 16/1/23 下午11:19
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableLodsve
@Import({RedisConfiguration.class, RedisImportSelector.class})
public @interface EnableRedis {
    /**
     * 数据源名称,required
     *
     * @return 数据源名称
     */
    String[] name();

    /**
     * 定时器使用的数据源，如果为空，则表示不用定时器
     *
     * @return 定时器使用的数据源
     */
    String timer() default "";
}