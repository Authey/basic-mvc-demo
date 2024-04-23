package basic.mvc.utility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BaseControllerTest extends BaseController {

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    @Before
    public void before() {
        request = new MockHttpServletRequest();
        super.request = request;
        response = new MockHttpServletResponse(); // No need to .reset() after committing the response in content acquiring
        request.setParameter("Key0", "Value");
        request.setParameter("Key1", "");
        request.setParameter("Key2", "     ");
    }

    @Test
    public void getPara0() {
        String res = this.getPara("Key0");
        assertEquals("Value", res);
    }

    @Test
    public void getPara1() {
        String res = this.getPara("Key1");
        assertEquals("", res);
    }

    @Test
    public void getPara2() {
        String res = this.getPara("Key2");
        assertEquals("", res);
    }

    @Test
    public void getPara3() {
        String res = this.getPara("Key0", "DefaultValue");
        assertEquals("Value", res);
    }

    @Test
    public void getPara4() {
        String res = this.getPara("Key1", "DefaultValue");
        assertEquals("DefaultValue", res);
    }

    @Test
    public void getPara5() {
        String res = this.getPara("Key2", "DefaultValue");
        assertEquals("DefaultValue", res);
    }

    @Test
    public void setAttr() {
        this.setAttr("Key", "Value");
        assertEquals("Value", request.getAttribute("Key"));
        request.removeAttribute("Key");
        assertNull(request.getAttribute("Key"));
    }

    @Test
    public void getAttr() {
        request.setAttribute("Key", "Value");
        assertEquals("Value", this.getAttr("Key"));
        request.removeAttribute("Key");
        assertNull(request.getAttribute("Key"));
    }

    @Test
    public void removeAttr() {
        request.setAttribute("Key", "Value");
        assertEquals("Value", request.getAttribute("Key"));
        this.removeAttr("Key");
        assertNull(request.getAttribute("Key"));
    }

    @Test
    public void renderText() throws Exception {
        this.renderText(response, "Text");
        assertEquals("text/plain;charset=UTF-8", response.getContentType());
        assertTrue(response.containsHeader("Pragma"));
        assertTrue(response.containsHeader("Cache-Control"));
        assertTrue(response.containsHeader("Expires"));
        assertEquals("No-cache", response.getHeader("Pragma"));
        assertEquals("no-cache", response.getHeader("Cache-Control"));
        assertEquals(0L, response.getDateHeader("Expires"));
        assertEquals("Text", response.getContentAsString());
    }

    @Test
    public void renderJson() throws Exception {
        this.renderJson(response, "Json");
        assertEquals("application/json;charset=UTF-8", response.getContentType());
        assertTrue(response.containsHeader("Pragma"));
        assertTrue(response.containsHeader("Cache-Control"));
        assertTrue(response.containsHeader("Expires"));
        assertEquals("No-cache", response.getHeader("Pragma"));
        assertEquals("no-cache", response.getHeader("Cache-Control"));
        assertEquals(0L, response.getDateHeader("Expires"));
        assertEquals("Json", response.getContentAsString());
    }

    @Test
    public void renderXml() throws Exception {
        this.renderXml(response, "Xml");
        assertEquals("text/xml;charset=UTF-8", response.getContentType());
        assertTrue(response.containsHeader("Pragma"));
        assertTrue(response.containsHeader("Cache-Control"));
        assertTrue(response.containsHeader("Expires"));
        assertEquals("No-cache", response.getHeader("Pragma"));
        assertEquals("no-cache", response.getHeader("Cache-Control"));
        assertEquals(0L, response.getDateHeader("Expires"));
        assertEquals("Xml", response.getContentAsString());
    }

    @Test
    public void renderHtml() throws Exception {
        this.renderHtml(response, "Html");
        assertEquals("text/html;charset=UTF-8", response.getContentType());
        assertTrue(response.containsHeader("Pragma"));
        assertTrue(response.containsHeader("Cache-Control"));
        assertTrue(response.containsHeader("Expires"));
        assertEquals("No-cache", response.getHeader("Pragma"));
        assertEquals("no-cache", response.getHeader("Cache-Control"));
        assertEquals(0L, response.getDateHeader("Expires"));
        assertEquals("Html", response.getContentAsString());
    }

    @Test
    public void render() throws Exception {
        this.render(response, "content/render;charset=UTF-8", "Render");
        assertEquals("content/render;charset=UTF-8", response.getContentType());
        assertTrue(response.containsHeader("Pragma"));
        assertTrue(response.containsHeader("Cache-Control"));
        assertTrue(response.containsHeader("Expires"));
        assertEquals("No-cache", response.getHeader("Pragma"));
        assertEquals("no-cache", response.getHeader("Cache-Control"));
        assertEquals(0L, response.getDateHeader("Expires"));
        assertEquals("Render", response.getContentAsString());
    }

    @Test
    public void ajaxDoneSuccess0() throws Exception {
        this.ajaxDoneSuccess(response, "Ajax Success");
        JSONObject json = JSON.parseObject(response.getContentAsString());
        assertEquals(1, json.get("state"));
        assertEquals(200, json.get("statusCode"));
        assertEquals("Ajax Success", json.get("msg"));
    }

    @Test
    public void ajaxDoneSuccess1() throws Exception {
        this.ajaxDoneSuccess(response, "");
        JSONObject json = JSON.parseObject(response.getContentAsString());
        assertEquals(1, json.get("state"));
        assertEquals(200, json.get("statusCode"));
        assertEquals("Success!", json.get("msg"));
    }

    @Test
    public void ajaxDoneFailure0() throws Exception {
        this.ajaxDoneFailure(response, "Ajax Failure");
        JSONObject json = JSON.parseObject(response.getContentAsString());
        assertEquals(0, json.get("state"));
        assertEquals(300, json.get("statusCode"));
        assertEquals("Ajax Failure", json.get("msg"));
    }

    @Test
    public void ajaxDoneFailure1() throws Exception {
        this.ajaxDoneFailure(response, "");
        JSONObject json = JSON.parseObject(response.getContentAsString());
        assertEquals(0, json.get("state"));
        assertEquals(300, json.get("statusCode"));
        assertEquals("Failure!", json.get("msg"));
    }

}
