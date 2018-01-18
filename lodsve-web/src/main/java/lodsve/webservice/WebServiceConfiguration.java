package lodsve.webservice;

import lodsve.core.condition.ConditionalOnClass;
import lodsve.core.properties.autoconfigure.annotations.EnableConfigurationProperties;
import lodsve.webservice.properties.WebServiceProperties;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * WebserviceConfiguration.
 *
 * @author sunhao(sunhao.java @ gmail.com)
 * @version 1.0 2018/1/13 上午2:51
 */
@ConditionalOnClass(CXFServlet.class)
@Configuration
@EnableConfigurationProperties(WebServiceProperties.class)
@ImportResource({"classpath*:META-INF/cxf/cxf.xml", "classpath*:META-INF/cxf/cxf-servlet.xml"})
public class WebServiceConfiguration {

    @Bean
    public WebServiceBeanFactoryPostProcessor webServiceBeanFactoryPostProcessor() {
        return new WebServiceBeanFactoryPostProcessor();
    }

    @Bean
    public LodsveWebserviceInitializer lodsveWebserviceInitializer(WebServiceProperties properties) {
        LodsveWebserviceInitializer initializingBean = new LodsveWebserviceInitializer();
        initializingBean.setProperties(properties);

        return initializingBean;
    }
}