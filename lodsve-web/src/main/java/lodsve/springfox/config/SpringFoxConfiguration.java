package lodsve.springfox.config;

import lodsve.core.properties.autoconfigure.annotations.EnableConfigurationProperties;
import lodsve.springfox.properties.SpringFoxProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 添加swagger部分的包扫描路径.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 16/3/23 下午4:16
 */
@Configuration
@EnableSwagger2
@EnableConfigurationProperties(SpringFoxProperties.class)
@ComponentScan(basePackages = {"lodsve.springfox"})
@Profile("springfox")
public class SpringFoxConfiguration {
}