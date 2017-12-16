package lodsve.redis.core.annotations;

import lodsve.redis.core.connection.RedisDataSourceBeanDefinitionFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * redis数据源注册.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 16/1/23 下午11:20
 */
public class RedisBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    private static final String DATASOURCE_NAME_ATTRIBUTE_NAME = "name";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableRedis.class.getName(), false));
        String[] names = attributes.getStringArray(DATASOURCE_NAME_ATTRIBUTE_NAME);

        for (String name : names) {
            if (beanDefinitionRegistry.containsBeanDefinition(name)) {
                continue;
            }

            beanDefinitionRegistry.registerBeanDefinition(name, new RedisDataSourceBeanDefinitionFactory(name).build());
        }
    }
}
