package lodsve.mvc.config;

import lodsve.core.autoconfigure.annotations.EnableConfigurationProperties;
import lodsve.properties.ServerProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * web mvc 配置,扫描包路径.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 16/1/28 上午10:58
 */
@Configuration
@ComponentScan(basePackages = {"lodsve.mvc"})
@EnableConfigurationProperties(ServerProperties.class)
public class WebMvcConfiguration {
}
