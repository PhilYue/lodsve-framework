package lodsve.mvc.resolver;

import lodsve.mvc.annotation.WebResource;
import lodsve.mvc.commons.FileWebInput;
import lodsve.mvc.commons.WebInput;
import lodsve.mvc.commons.WebOutput;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 为controller注入参数WebInput/WebOutput/FileWebInput.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 15/7/7 上午9:20
 */
public class WebResourceDataHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        WebResource inject = parameter.getParameterAnnotation(WebResource.class);
        Class<?> paramType = parameter.getParameterType();

        return inject != null && (WebInput.class.equals(paramType) || WebOutput.class.equals(paramType)
                || FileWebInput.class.equals(paramType));
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);

        Class<?> paramType = parameter.getParameterType();

        if (paramType.equals(WebInput.class)) {
            return new WebInput(request);
        } else if (paramType.equals(WebOutput.class)) {
            return new WebOutput(response);
        } else if (paramType.equals(FileWebInput.class)) {
            return new FileWebInput(request);
        }

        return null;
    }
}
