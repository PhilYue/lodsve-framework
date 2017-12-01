package lodsve.core.utils;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * 将代理类变成真实的类.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 15/9/2 下午5:21
 */
public class ProxyUtils {
    /**
     * 获取 目标对象
     *
     * @param proxy 代理对象
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T getTarget(T proxy) {
        if (Proxy.isProxyClass(proxy.getClass())) {
            return getJdkDynamicProxyTargetObject(proxy);
        } else if (ClassUtils.isCglibProxy(proxy)) {
            return getCglibProxyTargetObject(proxy);
        }

        return (T) AopUtils.getTargetClass(proxy);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getCglibProxyTargetObject(T proxy) {
        try {
            Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
            h.setAccessible(true);
            Object dynamicAdvisedInterceptor = h.get(proxy);

            Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
            advised.setAccessible(true);

            return (T) ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getJdkDynamicProxyTargetObject(T proxy) {
        try {
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            Object proxyObject = h.get(proxy);
            Field f = proxyObject.getClass().getDeclaredField("target");
            f.setAccessible(true);

            return (T) f.get(proxyObject);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
