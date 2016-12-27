package lodsve.mongodb.config;

import lodsve.core.autoconfigure.annotations.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;

/**
 * 基本配置.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 16/1/21 下午10:15
 */
@Configuration
@ComponentScan("lodsve.mongodb")
@EnableConfigurationProperties(MongoProperties.class)
public class MongoConfiguration {
    @Bean
    public DefaultMongoTypeMapper defaultMongoTypeMapper() {
        return new DefaultMongoTypeMapper();
    }
}
