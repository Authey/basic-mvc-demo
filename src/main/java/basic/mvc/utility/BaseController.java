package basic.mvc.utility;

import basic.mvc.utility.exception.NoSuchElementFoundException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseController {

    @Resource
    public HttpServletRequest request;

    @Resource
    public HttpServletResponse response;

    public final Logger logger = Logger.getLogger(this.getClass());

    public String getRootPath() {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
    }

    public String getPara(String name) throws Exception {
        try {
            String str = request.getParameter(name);
            return escape(str);
        } catch (NullPointerException e) {
            throw new NoSuchElementFoundException("No Such Parameter Found in This Request");
        }
    }

    public String getPara(String name, String defaultValue) {
        try {
            String str = request.getParameter(name);
            return StringUtils.isNotBlank(str) ? escape(str) : defaultValue;
        } catch (NullPointerException e) {
            logger.error("No Such Parameter Found in This Request, Set to Default Value");
            return defaultValue;
        }
    }

    public void setAttr(String key, Object value) {
        request.setAttribute(key, value);
    }

    public String getAttr(String name) {
        return (String) request.getAttribute(name);
    }

    public void removeAttr(String name) {
        request.removeAttribute(name);
    }

    public void renderText(String text) {
        this.render("text/plain;charset=UTF-8", text);
    }

    public void renderJson(String json) {
        this.render("application/json;charset=UTF-8", json);
    }

    public void renderXml(String xml) {
        this.render("text/xml;charset=UTF-8", xml);
    }

    public void renderHtml(String html) {
        this.render("text/html;charset=UTF-8", html);
    }

    public void render(String contentType, String text) {
        response.setContentType(contentType);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0L);
        try {
            response.getWriter().write(text);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void ajaxDoneSuccess(String message) {
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        result.put("state", 1);
        result.put("statusCode", 200);
        if (StringUtils.isBlank(message)) {
            result.put("msg", "Success!");
        } else {
            result.put("msg", message);
        }
        this.renderJson(result.toString());
    }

    public void ajaxDoneFailure(String message) {
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        result.put("state", 0);
        result.put("statusCode", 300);
        if (StringUtils.isBlank(message)) {
            result.put("msg", "Failure!");
        } else {
            result.put("msg", message);
        }
        this.renderJson(result.toString());
    }

    private String escape(String str) {
        str = str.trim();
        if (StringUtils.isNotBlank(str)) {
            str = StringEscapeUtils.escapeSql(str);
            str = HtmlUtils.htmlEscape(str);
            return JavaScriptUtils.javaScriptEscape(str);
        }
        return str;
    }

}
