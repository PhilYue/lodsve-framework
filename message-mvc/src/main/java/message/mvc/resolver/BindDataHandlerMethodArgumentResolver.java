package message.mvc.resolver;

import message.mvc.annotation.Bind;
import message.utils.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析加了注解{@link message.mvc.annotation.Bind}的参数.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0
 * @createTime 2015-1-29 21:49
 */
public class BindDataHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Bind bind = parameter.getParameterAnnotation(Bind.class);

        return bind != null && StringUtils.isNotEmpty(bind.value());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //前缀
        String name = parameter.getParameterAnnotation(Bind.class).value();
        //类型
        Class<?> clazz = parameter.getParameterType();
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        //所有参数
        Enumeration<String> es = request.getParameterNames();
        Map<String, String> paramsMap = new HashMap<String, String>();
        for (; es.hasMoreElements(); ) {
            String key = es.nextElement();
            String value = request.getParameter(key);

            if (key.startsWith(name + ".")) {
                String field = StringUtils.removeStart(key, name + ".");
                paramsMap.put(field, value);
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        //转成json
        String paramsJson = objectMapper.writeValueAsString(paramsMap);

        return objectMapper.readValue(paramsJson, clazz);
    }
}
