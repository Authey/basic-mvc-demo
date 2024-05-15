package basic.mvc.demo.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "classpath:spring-mvc.xml")
public class StatusControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void before() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void show0() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/status/404"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void show1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/status/405"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void show2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/status/500"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test(expected = NestedServletException.class)
    public void show3() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/status/444"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

}
