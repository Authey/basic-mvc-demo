package basic.mvc.utility;

import basic.mvc.utility.exception.NoSuchElementFoundException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class BaseControllerTest extends BaseController {

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private final MockHttpServletResponse response = new MockHttpServletResponse(); // No need to .reset() after committing the response in content acquiring

    @Before
    public void before() {
        super.request = request;
        super.response = response;
        request.setParameter("Key0", "Value");
        request.setParameter("Key1", "");
        request.setParameter("Key2", "     ");
    }

    @Test
    public void getContextPath() {
        request.setContextPath("/context");
        request.setScheme("https");
        request.setServerName("localhost");
        request.setServerPort(9134);
        String root = this.getRootPath();
        assertEquals("https://localhost:9134/context", root);
    }

    @Test(expected = NoSuchElementFoundException.class)
    public void getPara0() {
        this.getPara("Key");
    }

    @Test
    public void getPara1() {
        String res = this.getPara("Key0");
        assertEquals("Value", res);
    }

    @Test
    public void getPara2() {
        String res = this.getPara("Key1");
        assertEquals("", res);
    }

    @Test
    public void getPara3() {
        String res = this.getPara("Key2");
        assertEquals("", res);
    }

    @Test
    public void getPara4() {
        String res = this.getPara("Key", "DefaultValue");
        assertEquals("DefaultValue", res);
    }

    @Test
    public void getPara5() {
        String res = this.getPara("Key0", "DefaultValue");
        assertEquals("Value", res);
    }

    @Test
    public void getPara6() {
        String res = this.getPara("Key1", "DefaultValue");
        assertEquals("DefaultValue", res);
    }

    @Test
    public void getPara7() {
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
        this.renderText("Text");
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
        this.renderJson("Json");
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
        this.renderXml("Xml");
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
        this.renderHtml("Html");
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
        this.render("content/render;charset=UTF-8", "Render");
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
        this.ajaxDoneSuccess("Ajax Success");
        JSONObject json = JSON.parseObject(response.getContentAsString());
        assertEquals(1, json.get("state"));
        assertEquals(200, json.get("statusCode"));
        assertEquals("Ajax Success", json.get("msg"));
    }

    @Test
    public void ajaxDoneSuccess1() throws Exception {
        this.ajaxDoneSuccess("");
        JSONObject json = JSON.parseObject(response.getContentAsString());
        assertEquals(1, json.get("state"));
        assertEquals(200, json.get("statusCode"));
        assertEquals("Success!", json.get("msg"));
    }

    @Test
    public void ajaxDoneFailure0() throws Exception {
        this.ajaxDoneFailure("Ajax Failure");
        JSONObject json = JSON.parseObject(response.getContentAsString());
        assertEquals(0, json.get("state"));
        assertEquals(300, json.get("statusCode"));
        assertEquals("Ajax Failure", json.get("msg"));
    }

    @Test
    public void ajaxDoneFailure1() throws Exception {
        this.ajaxDoneFailure("");
        JSONObject json = JSON.parseObject(response.getContentAsString());
        assertEquals(0, json.get("state"));
        assertEquals(300, json.get("statusCode"));
        assertEquals("Failure!", json.get("msg"));
    }

}
