package basic.mvc.utility;

import basic.mvc.demo.user.po.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class BaseInterceptorTest {

    private final BaseInterceptor baseInterceptor = new BaseInterceptor();

    private final MockHttpServletRequest request = new MockHttpServletRequest();

    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @Test
    public void preHandle0() throws Exception {
        assertFalse(baseInterceptor.preHandle(request, response, null));
    }

    @Test
    public void preHandle1() throws Exception {
        request.getSession().setAttribute("user", new User());
        assertTrue(baseInterceptor.preHandle(request, response, null));
        request.getSession().removeAttribute("user");
        assertFalse(baseInterceptor.preHandle(request, response, null));
    }

    @Test
    public void preHandle2() throws Exception {
        request.getSession().setAttribute("user", null);
        assertFalse(baseInterceptor.preHandle(request, response, null));
    }

}
