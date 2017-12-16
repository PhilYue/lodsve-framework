package lodsve.core.exception;

import lodsve.core.properties.Env;
import lodsve.core.properties.i18n.ResourceBundleHolder;
import lodsve.core.utils.PropertyPlaceholderHelper;
import lodsve.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 异常处理,返回前端类似于<code>{"code": 10001,"message": "test messages"}</code>的json数据.
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 15/8/14 上午9:48
 */
@ControllerAdvice
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);
    private static final ExceptionData DEFAULT_EXCEPTION_DATA = new ExceptionData(HttpStatus.BAD_REQUEST.value(), "发生未知的错误！");
    private static final Integer ASSERT_ERROR_CODE = 199999;

    /**
     * 加载了所有的资源文件信息.
     */
    private ResourceBundleHolder resourceBundleHolder = new ResourceBundleHolder();

    @PostConstruct
    public void init() throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        // 1. 框架异常
        List<Resource> resources = new ArrayList<>();
        try {
            resources.addAll(Arrays.asList(resolver.getResources("classpath*:/META-INF/error/*.properties")));
        } catch (IOException e) {
            logger.error("resolver resource:'{classpath*:/META-INF/error/*.properties}' is error!", e);
            e.printStackTrace();
        }

        // 2. 项目异常
        String folderPath = "error";
        Resource resource = Env.getFileConfig(folderPath);

        try {
            resources.addAll(Arrays.asList(resolver.getResources("file:" + resource.getFile().getAbsolutePath() + "/*.properties")));
        } catch (IOException e) {
            logger.error("resolver resource:'{" + resource + "}' is error!", e);
            e.printStackTrace();
        }

        for (Resource r : resources) {
            String filePath;
            String fileName = r.getFilename();
            if (r instanceof FileSystemResource) {
                filePath = r.getFile().getAbsolutePath();
            } else {
                filePath = r.getURL().toString();
            }

            if (!StringUtils.contains(fileName, "_")) {
                this.resourceBundleHolder.loadMessageResource(filePath, 1);
            }
        }
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionData handleException(Exception ex, NativeWebRequest webRequest) throws Exception {
        if (ex == null) {
            return DEFAULT_EXCEPTION_DATA;
        }

        if (ex instanceof ApplicationException) {
            // 框架定义的异常
            return handleWebException(ex, webRequest);
        } else if (ex instanceof IllegalArgumentException) {
            // spring assert exception
            return new ExceptionData(ASSERT_ERROR_CODE, ex.getMessage());
        }

        return new ExceptionData(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    private ExceptionData handleWebException(Exception ex, NativeWebRequest webRequest) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Throwable exception = getHasInfoException(ex);

        ExceptionInfo exceptionInfo = ((ApplicationException) exception).getInfo();
        String message;
        Integer code = exceptionInfo.getCode();

        try {
            message = resourceBundleHolder.getResourceBundle(request.getLocale()).getString(code.toString());
            message = PropertyPlaceholderHelper.replace(message, message, exceptionInfo.getArgs());
        } catch (Exception e) {
            logger.error("根据异常编码获取异常描述信息发生异常，errorCode：" + code);
            message = exceptionInfo.getMessage();
        }

        if (StringUtils.isEmpty(message)) {
            message = "发生未知的错误！";
        }

        logger.error(String.format("exception code:[%s],exception message: %s", code, message), ex);

        return new ExceptionData(code, message);
    }

    private Throwable getHasInfoException(Throwable throwable) {
        Throwable exception = null;

        if (throwable instanceof ApplicationException) {
            exception = throwable;
        }

        Throwable childThrowable;
        if (throwable instanceof UndeclaredThrowableException) {
            childThrowable = ((UndeclaredThrowableException) throwable).getUndeclaredThrowable();
        } else {
            childThrowable = throwable.getCause();
        }
        if (childThrowable != null) {
            Throwable childExp = getHasInfoException(childThrowable);
            if (childExp != null) {
                return childExp;
            }
        }

        return exception;
    }

    private static class ExceptionData implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 异常编码
         */
        private Integer code;

        /**
         * 后台异常描述，正常不应该把后台异常描述反馈给前台用户
         */
        private String message;

        public ExceptionData() {
        }

        public ExceptionData(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
