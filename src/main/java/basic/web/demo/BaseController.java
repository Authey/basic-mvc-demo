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

}
