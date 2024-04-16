package basic.web.demo;

import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BaseController {

    @Resource
    protected HttpServletRequest request;

    protected final Logger logger = Logger.getLogger(this.getClass());

    protected void renderJson(HttpServletResponse response, String text) {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        try {
            response.getWriter().write(text);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected Object classCast(Class<?> clazz, String str) {
        if (clazz.equals(Byte.class)) {
            return Byte.parseByte(str);
        } else if (clazz.equals(Short.class)) {
            return Short.parseShort(str);
        } else if (clazz.equals(Integer.class)) {
            return Integer.parseInt(str);
        } else if (clazz.equals(Long.class)) {
            return Long.parseLong(str);
        } else if (clazz.equals(Float.class)) {
            return Float.parseFloat(str);
        } else if (clazz.equals(Double.class)) {
            return Double.parseDouble(str);
        } else if (clazz.equals(Boolean.class)) {
            return Boolean.parseBoolean(str);
        }
        return clazz.cast(str);
    }

}
