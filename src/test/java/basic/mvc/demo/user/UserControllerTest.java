package basic.mvc.demo.user;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "classpath:spring-mvc.xml")
@Transactional
public class UserControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void before() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void index0() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/index"))
                .andExpect(status().isOk())
                .andExpect(request().attribute("type", "Login"))
                .andDo(print());
    }

    @Test
    public void index1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/index"))
                .andExpect(status().isOk())
                .andExpect(request().attribute("type", "Login"))
                .andDo(print());
    }

    @Test
    public void login() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/login")
                        .param("username", "Username")
                        .param("password", "Password"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void logout() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/logout"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void enroll() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/enroll")
                        .param("username", "Username")
                        .param("password", "Password"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void load() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/load")
                        .param("page", "1")
                        .param("rows", "1"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void remove() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/remove")
                        .param("username", "Username"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void centre0() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/centre"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void centre1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/centre"))
                .andExpect(status().isOk())
                .andDo(print());
    }

}
