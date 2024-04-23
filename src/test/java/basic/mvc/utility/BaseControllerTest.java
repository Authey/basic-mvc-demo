package basic.mvc.utility;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BaseControllerTest extends BaseController {

    MockHttpServletResponse response;

    @Before
    public void before() {
        this.request = mock(HttpServletRequest.class);
        this.response = new MockHttpServletResponse(); // No need to .reset() after committing the response in content acquiring
        when(this.request.getParameter("Key0")).thenReturn("Value");
        when(this.request.getParameter("Key1")).thenReturn("");
        when(this.request.getParameter("Key2")).thenReturn("     ");
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

    @After
    public void after() {
        reset(request);
    }

}
