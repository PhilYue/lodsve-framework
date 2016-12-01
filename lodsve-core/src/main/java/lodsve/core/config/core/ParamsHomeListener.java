package lodsve.core.config.core;

import lodsve.core.config.ini.IniLoader;
import lodsve.core.config.properties.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 读取web.xml中设置的context-param[paramsHome].
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0
 * @createTime 2015-1-5 10:00
 * @see InitializeConfigurationPath
 */
public class ParamsHomeListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ParamsHomeListener.class);

    private static final String PARAMS_HOME = "paramsHome";

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String paramsHome = servletContextEvent.getServletContext().getInitParameter(PARAMS_HOME);
        logger.debug("get init parameter '{}' from web.xml is '{}'", PARAMS_HOME, paramsHome);

        InitializeConfigurationPath.setWebXmlParamsHome(paramsHome);

        try {
            ConfigurationLoader.init();
            IniLoader.init();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
