package lodsve.mybatis.configs;

import lodsve.core.utils.StringUtils;
import lodsve.mybatis.configs.annotations.EnableMyBatis;
import lodsve.mybatis.datasource.builder.RdbmsDataSourceBeanDefinitionBuilder;
import lodsve.mybatis.datasource.dynamic.DynamicDataSource;
import lodsve.mybatis.exception.MyBatisException;
import lodsve.mybatis.key.IDGenerator;
import lodsve.mybatis.key.mysql.MySQLIDGenerator;
import lodsve.mybatis.key.oracle.OracleIDGenerator;
import lodsve.mybatis.plugins.pagination.PaginationInterceptor;
import lodsve.mybatis.type.TypeHandlerScanner;
import org.apache.commons.lang.ArrayUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.flywaydb.core.Flyway;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建多数据源相关bean.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version 1.0 2017/12/14 下午4:42
 */
public class MyBatisConfigurationBuilder {
    private AnnotationAttributes attributes;
    private AnnotationMetadata metadata;
    private boolean useFlyway;
    private String migration;

    private MyBatisConfigurationBuilder(AnnotationMetadata metadata) {
        this.metadata = metadata;

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableMyBatis.class.getName(), false));
        Assert.notNull(attributes, String.format("@%s is not present on importing class '%s' as expected", EnableMyBatis.class.getName(), metadata.getClassName()));
        this.attributes = attributes;
        this.useFlyway = attributes.getBoolean(Constant.USE_FLYWAY_ATTRIBUTE_NAME);
        this.migration = attributes.getString(Constant.MIGRATION_ATTRIBUTE_NAME);
    }

    private Map<String, BeanDefinition> generateDataSource() {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>(16);
        AnnotationAttributes[] dataSources = attributes.getAnnotationArray(Constant.DATA_SOURCE_ATTRIBUTE_NAME);
        if (null == dataSources || dataSources.length == 0) {
            throw new MyBatisException(102005, "can't find any datasource!");
        }

        // 组装一些信息
        DataSourceBean dataSourceBean = new DataSourceBean(dataSources);
        String defaultDataSourceKey = dataSourceBean.getDefaultDataSourceKey();
        BeanDefinition defaultDataSource = dataSourceBean.getDefaultDataSource();
        beanDefinitions.putAll(dataSourceBean.getBeanDefinitions());

        // 动态数据源
        BeanDefinitionBuilder dynamicDataSource = BeanDefinitionBuilder.genericBeanDefinition(DynamicDataSource.class);
        dynamicDataSource.addConstructorArgValue(dataSourceBean.getDataSourceNames());
        dynamicDataSource.addConstructorArgValue(defaultDataSourceKey);
        beanDefinitions.put(Constant.DATA_SOURCE_BEAN_NAME, dynamicDataSource.getBeanDefinition());

        // 生成IDGenerator
        String driverClassName = (String) defaultDataSource.getPropertyValues().get("driverClassName");
        Class<? extends IDGenerator> clazz;
        if (StringUtils.contains(driverClassName, Constant.MYSQL)) {
            clazz = MySQLIDGenerator.class;
        } else if (StringUtils.contains(driverClassName, Constant.ORACLE)) {
            clazz = OracleIDGenerator.class;
        } else {
            clazz = MySQLIDGenerator.class;
        }

        BeanDefinitionBuilder idGenerator = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        idGenerator.addPropertyReference("dataSource", Constant.DATA_SOURCE_BEAN_NAME);
        beanDefinitions.put("idGenerator", idGenerator.getBeanDefinition());

        return beanDefinitions;
    }

    private Map<String, BeanDefinition> findFlyWayBeanDefinitions() {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>(16);
        if (!useFlyway) {
            return beanDefinitions;
        }

        BeanDefinitionBuilder flywayBean = BeanDefinitionBuilder.genericBeanDefinition(Flyway.class);
        flywayBean.setInitMethodName("migrate");
        flywayBean.addPropertyReference("dataSource", Constant.DATA_SOURCE_BEAN_NAME);
        flywayBean.addPropertyValue("locations", migration);

        beanDefinitions.put(Constant.FLYWAY_BEAN_NAME, flywayBean.getBeanDefinition());

        return beanDefinitions;
    }

    private Map<String, BeanDefinition> findMyBatisBeanDefinitions() {
        String[] enumsLocations = attributes.getStringArray(Constant.ENUMS_LOCATIONS_ATTRIBUTE_NAME);
        String[] basePackages = attributes.getStringArray(Constant.BASE_PACKAGES_ATTRIBUTE_NAME);
        AnnotationAttributes[] plugins = attributes.getAnnotationArray(Constant.PLUGINS_ATTRIBUTE_NAME);

        if (ArrayUtils.isEmpty(enumsLocations)) {
            enumsLocations = findDefaultPackage(metadata);
        }
        if (ArrayUtils.isEmpty(basePackages)) {
            basePackages = findDefaultPackage(metadata);
        }

        Map<String, BeanDefinition> beanDefinitions = new HashMap<>(16);

        BeanDefinitionBuilder sqlSessionFactoryBean = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionFactoryBean.class);

        if (useFlyway) {
            sqlSessionFactoryBean.addDependsOn(Constant.FLYWAY_BEAN_NAME);
        }

        sqlSessionFactoryBean.addPropertyReference("dataSource", Constant.DATA_SOURCE_BEAN_NAME);
        sqlSessionFactoryBean.addPropertyValue("mapperLocations", "classpath*:/META-INF/mybatis/**/*Mapper.xml");
        sqlSessionFactoryBean.addPropertyValue("configLocation", "classpath:/META-INF/mybatis/mybatis.xml");
        TypeHandlerScanner scanner = new TypeHandlerScanner();
        sqlSessionFactoryBean.addPropertyValue("typeHandlers", scanner.find(StringUtils.join(enumsLocations, ",")));
        List<Interceptor> pluginsList = new ArrayList<>(plugins.length);
        List<Class<? extends Interceptor>> clazz = new ArrayList<>(plugins.length);
        for (AnnotationAttributes plugin : plugins) {
            Class<? extends Interceptor> pluginClass = plugin.getClass("value");
            AnnotationAttributes[] params = plugin.getAnnotationArray("params");

            clazz.add(pluginClass);
            Interceptor interceptor = BeanUtils.instantiate(pluginClass);
            BeanWrapper beanWrapper = new BeanWrapperImpl(interceptor);
            for (AnnotationAttributes param : params) {
                String key = param.getString("key");
                String value = param.getString("value");

                PropertyDescriptor descriptor = beanWrapper.getPropertyDescriptor(key);
                Method writeMethod = descriptor.getWriteMethod();
                Method readMethod = descriptor.getReadMethod();
                writeMethod.setAccessible(true);
                try {
                    Class<?> returnType = readMethod.getReturnType();
                    Object valueObject = value;
                    if (Integer.class.equals(returnType) || int.class.equals(returnType)) {
                        valueObject = Integer.valueOf(value);
                    } else if (Long.class.equals(returnType) || long.class.equals(returnType)) {
                        valueObject = Long.valueOf(value);
                    } else if (Boolean.class.equals(returnType) || boolean.class.equals(returnType)) {
                        valueObject = Boolean.valueOf(value);
                    } else if (Double.class.equals(returnType) || double.class.equals(returnType)) {
                        valueObject = Double.valueOf(value);
                    }

                    writeMethod.invoke(interceptor, valueObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            pluginsList.add(interceptor);
        }
        if (!clazz.contains(PaginationInterceptor.class)) {
            pluginsList.add(BeanUtils.instantiate(PaginationInterceptor.class));
        }

        sqlSessionFactoryBean.addPropertyValue("plugins", pluginsList);

        BeanDefinitionBuilder scannerConfigurerBean = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
        scannerConfigurerBean.addPropertyValue("basePackage", StringUtils.join(basePackages, ","));
        scannerConfigurerBean.addPropertyValue("annotationClass", Repository.class);
        scannerConfigurerBean.addPropertyValue("sqlSessionFactoryBeanName", "sqlSessionFactory");

        beanDefinitions.put("sqlSessionFactory", sqlSessionFactoryBean.getBeanDefinition());
        beanDefinitions.put("mapperScannerConfigurer", scannerConfigurerBean.getBeanDefinition());

        return beanDefinitions;
    }

    private String[] findDefaultPackage(AnnotationMetadata annotationMetadata) {
        String className = annotationMetadata.getClassName();
        try {
            Class<?> clazz = ClassUtils.forName(className, getClass().getClassLoader());
            return new String[]{ClassUtils.getPackageName(clazz)};
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static class Builder {
        private AnnotationMetadata metadata;

        Builder setMetadata(AnnotationMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        Map<String, BeanDefinition> build() {
            Map<String, BeanDefinition> beanDefinitions = new HashMap<>(16);
            MyBatisConfigurationBuilder builder = new MyBatisConfigurationBuilder(metadata);

            beanDefinitions.putAll(builder.generateDataSource());
            beanDefinitions.putAll(builder.findFlyWayBeanDefinitions());
            beanDefinitions.putAll(builder.findMyBatisBeanDefinitions());

            return beanDefinitions;
        }
    }

    private static class DataSourceBean {
        private String defaultDataSourceKey;
        private BeanDefinition defaultDataSource;
        private Map<String, BeanDefinition> beanDefinitions;
        private List<String> dataSourceNames;

        DataSourceBean(AnnotationAttributes[] dataSources) {
            beanDefinitions = new HashMap<>(dataSources.length);
            dataSourceNames = new ArrayList<>(dataSources.length);

            for (int i = 0; i < dataSources.length; i++) {
                AnnotationAttributes attr = dataSources[i];

                String name = attr.getString(Constant.DATA_SOURCE_NAME_ATTRIBUTE_NAME);
                BeanDefinition dsBeanDefinition = new RdbmsDataSourceBeanDefinitionBuilder(name).build();

                if (i == 0) {
                    defaultDataSourceKey = attr.getString(Constant.DATA_SOURCE_NAME_ATTRIBUTE_NAME);
                    defaultDataSource = dsBeanDefinition;
                }

                beanDefinitions.put(name, dsBeanDefinition);
                dataSourceNames.add(name);
            }
        }

        String getDefaultDataSourceKey() {
            return defaultDataSourceKey;
        }

        BeanDefinition getDefaultDataSource() {
            return defaultDataSource;
        }

        Map<String, BeanDefinition> getBeanDefinitions() {
            return beanDefinitions;
        }

        List<String> getDataSourceNames() {
            return dataSourceNames;
        }
    }
}
