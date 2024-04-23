package basic.mvc.utility;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseController {

    @Resource
    public HttpServletRequest request;

    public SqlSessionFactoryBean sqlSessionFactoryBean;

    public final Logger logger = Logger.getLogger(this.getClass());

    public String getPara(String name) {
        return this.getPara(name, "");
    }

    public String getPara(String name, String defaultValue) {
        String str = this.request.getParameter(name);
        str = str.trim();
        str = StringEscapeUtils.escapeSql(str);
        str = HtmlUtils.htmlEscape(str);
        str = JavaScriptUtils.javaScriptEscape(str);
        return StringUtils.isNotBlank(str) ? str : defaultValue;
    }

    public void setAttr(String key, Object value) {
        this.request.setAttribute(key, value);
    }

    public String getAttr(String name) {
        return (String) this.request.getAttribute(name);
    }

    public void removeAttr(String name) {
        this.request.removeAttribute(name);
    }

    public void renderText(HttpServletResponse response, String text) {
        this.render(response, "text/plain;charset=UTF-8", text);
    }

    public void renderJson(HttpServletResponse response, String text) {
        this.render(response, "application/json;charset=UTF-8", text);
    }

    public void renderXml(HttpServletResponse response, String text) {
        this.render(response, "text/xml;charset=UTF-8", text);
    }

    public void renderHtml(HttpServletResponse response, String text) {
        this.render(response, "text/html;charset=UTF-8", text);
    }

    public void render(HttpServletResponse response, String contentType, String text) {
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

    public void ajaxDoneSuccess(HttpServletResponse response, String message) {
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        result.put("state", 1);
        result.put("statusCode", 200);
        if (StringUtils.isBlank(message)) {
            result.put("msg", "Success!");
        } else {
            result.put("msg", message);
        }
        this.renderJson(response, result.toString());
    }

    public void ajaxDoneFailure(HttpServletResponse response, String message) {
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        result.put("state", 0);
        result.put("statusCode", 300);
        if (StringUtils.isBlank(message)) {
            result.put("msg", "Failure!");
        } else {
            result.put("msg", message);
        }
        this.renderJson(response, result.toString());
    }

}
