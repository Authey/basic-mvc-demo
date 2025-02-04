package basic.mvc.demo.home;

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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "classpath:spring-mvc.xml")
public class HomeControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void before() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void base() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isFound())
                .andDo(print());
    }

    @Test
    public void home() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/home"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void index0() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/index"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void index1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/index"))
                .andExpect(status().isOk())
                .andDo(print());
    }

}
