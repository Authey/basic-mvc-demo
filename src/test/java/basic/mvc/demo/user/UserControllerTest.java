package basic.mvc.demo.user;

import basic.mvc.demo.user.po.User;
import basic.mvc.utility.CryptoUtils;
import org.hamcrest.core.StringContains;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = "classpath:spring-mvc.xml")
@Transactional
public class UserControllerTest {

    private MockMvc mvc;

    private static final MockHttpSession session = new MockHttpSession();

    private static final User user = new User();

    private static final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    private static final String userId = UUID.randomUUID().toString();

    private static final String password = CryptoUtils.toHex(CryptoUtils.hash("Password", "MD5")).toUpperCase();

    private static final String classpath = "D:/Java/Projects/basic-mvc-demo/target/classes";

    @Autowired
    private WebApplicationContext context;

    @BeforeClass
    public static void setup() {
        user.setId(userId);
        user.setUsername("UserOne");
        user.setPassword(password);
        user.setAuthLevel("SUPER");
        session.setAttribute("user", user);
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-jdbc.xml");
        DataSource dataSource = (DataSource) context.getBean("oracleDataSource");
        jdbcTemplate.setDataSource(dataSource);
        jdbcTemplate.update("INSERT INTO SYS_USER (ID, USERNAME, PASSWORD, AUTH_LEVEL) VALUES (?, ?, ?, ?)", userId, "UserOne", password, "SUPER");
        jdbcTemplate.update("INSERT INTO SYS_USER (ID, USERNAME, PASSWORD, AUTH_LEVEL) VALUES (?, ?, ?, ?)", UUID.randomUUID().toString(), "UserTwo", password, "ADMIN");
        jdbcTemplate.update("INSERT INTO SYS_USER (ID, USERNAME, PASSWORD, AUTH_LEVEL) VALUES (?, ?, ?, ?)", UUID.randomUUID().toString(), "UserThree", password, "GUEST");
    }

    @AfterClass
    public static void clean() {
        jdbcTemplate.update("DELETE FROM SYS_USER WHERE USERNAME = ?", "UserOne");
        jdbcTemplate.update("DELETE FROM SYS_USER WHERE USERNAME = ?", "UserTwo");
        jdbcTemplate.update("DELETE FROM SYS_USER WHERE USERNAME = ?", "UserThree");
    }

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
        mvc.perform(MockMvcRequestBuilders.get("/user/index")
                        .param("type", "Enroll"))
                .andExpect(status().isOk())
                .andExpect(request().attribute("type", "Enroll"))
                .andDo(print());
    }

    @Test
    public void index2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/index")
                        .param("type", "Test"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/status/404"))
                .andDo(print());
    }

    @Test
    public void index3() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/index")
                        .param("type", "Logout"))
                .andExpect(status().isOk())
                .andExpect(request().attribute("type", "Login"))
                .andDo(print());
    }

    @Test
    public void index4() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/index")
                        .param("type", "Login")
                        .session(session))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andDo(print());
    }

    @Test
    public void index5() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/index")
                        .param("type", "Enroll")
                        .session(session))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andDo(print());
    }

    @Test
    public void index6() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/index")
                        .param("type", "Logout")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(request().attribute("type", "Logout"))
                .andDo(print());
    }

    @Test
    public void index7() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/index")
                        .param("type", "Manage")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(request().attribute("type", "Manage"))
                .andExpect(request().attribute("auth", "SUPER"))
                .andDo(print());
    }

    @Test
    public void index8() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/index")
                        .param("alert", "Alert Message"))
                .andExpect(status().isOk())
                .andExpect(request().attribute("alert", "Alert Message"))
                .andDo(print());
    }

    @Test
    public void index9() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/index")
                        .param("alert", "Alert Message")
                        .session(session))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"))
                .andDo(print());
    }

    @Test
    public void index10() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/index")
                        .param("type", "Manage")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(request().attribute("type", "Manage"))
                .andDo(print());
    }

    @Test
    public void login0() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/login")
                        .param("username", "UserOne")
                        .param("password", "Password"))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Password Unmatched")))
                .andDo(print());
    }

    @Test
    public void login1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/login")
                        .param("username", "User")
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("No User Found")))
                .andDo(print());
    }

    @Test
    public void login2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/login")
                        .param("username", "UserOne")
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Success")))
                .andDo(print());
    }

    @Test
    public void logout0() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/logout"))
                .andExpect(status().isOk())
                .andDo(print());
        assertNotNull(session.getAttribute("user"));
    }

    @Test
    public void logout1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/logout")
                        .session(session))
                .andExpect(status().isOk())
                .andDo(print());
        assertNull(session.getAttribute("user"));
        session.setAttribute("user", user);
        assertNotNull(session.getAttribute("user"));
    }

    @Test
    public void enroll0() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/enroll")
                        .param("username", "User")
                        .param("password", "Pass"))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Password Is Too Short")))
                .andDo(print());
    }

    @Test
    public void enroll1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/enroll")
                        .param("username", "User")
                        .param("password", "PasswordPasswordPassword"))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Password Is Too Long")))
                .andDo(print());
    }

    @Test
    public void enroll2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/enroll")
                        .param("username", "UserOne")
                        .param("password", "Password"))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Duplicate Username")))
                .andDo(print());
    }

    @Test
    public void enroll3() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/enroll")
                        .param("username", "")
                        .param("password", "Password"))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Empty Username")))
                .andDo(print());
    }

    @Test
    public void enroll4() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/enroll")
                        .param("username", "User")
                        .param("password", "Password"))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("1")))
                .andDo(print());
    }

    @Test
    public void load0() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/load")
                        .param("page", "1")
                        .param("rows", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("[{")))
                .andDo(print());
    }

    @Test
    public void load1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/load")
                        .param("page", "2")
                        .param("rows", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("[]")))
                .andDo(print());
    }

    @Test
    public void load2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/load")
                        .param("page", "0")
                        .param("rows", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Unexpected Parameters")))
                .andDo(print());
    }

    @Test
    public void load3() throws Exception {
        String res = mvc.perform(MockMvcRequestBuilders.post("/user/load")
                        .param("page", "1")
                        .param("rows", "100") // For Compatability When Database Has More Users
                        .param("username", "UserOne"))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("\"records\":1"))) // Comment This For Rigour When Database Has More Users
                .andExpect(content().string(new StringContains("UserOne")))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        assertFalse(res.contains("UserTwo"));
    }

    @Test
    public void load4() throws Exception {
        String res = mvc.perform(MockMvcRequestBuilders.post("/user/load")
                        .param("page", "1")
                        .param("rows", "100") // For Compatability When Database Has More Users
                        .param("username", "UserT"))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("\"records\":2"))) // Comment This For Rigour When Database Has More Users
                .andExpect(content().string(new StringContains("UserTwo")))
                .andExpect(content().string(new StringContains("UserThree")))
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        assertFalse(res.contains("UserOne"));
    }

    @Test
    public void remove0() throws Exception {
        user.setAuthLevel("GUEST");
        assertEquals("GUEST", user.getAuthLevel());
        mvc.perform(MockMvcRequestBuilders.post("/user/remove")
                        .param("username", "UserThree")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Unauthorised Deletion")))
                .andDo(print());
        user.setAuthLevel("SUPER");
        assertEquals("SUPER", user.getAuthLevel());
    }

    @Test
    public void remove1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/remove")
                        .param("username", "UserTwo")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Cannot Delete Admin Or Super User")))
                .andDo(print());
    }

    @Test
    public void remove2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/remove")
                        .param("username", "User")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("No Username Matched")))
                .andDo(print());
    }

    @Test
    public void remove3() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/remove")
                        .param("username", "UserThree")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("1")))
                .andDo(print());
    }

    @Test
    public void alter0() throws Exception {
        user.setAuthLevel("ADMIN");
        assertEquals("ADMIN", user.getAuthLevel());
        mvc.perform(MockMvcRequestBuilders.post("/user/alter")
                        .param("username", "UserThree")
                        .param("type", "Promote")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Unauthorised Operation Promote")))
                .andDo(print());
        user.setAuthLevel("SUPER");
        assertEquals("SUPER", user.getAuthLevel());
    }

    @Test
    public void alter1() throws Exception {
        user.setAuthLevel("ADMIN");
        assertEquals("ADMIN", user.getAuthLevel());
        mvc.perform(MockMvcRequestBuilders.post("/user/alter")
                        .param("username", "UserTwo")
                        .param("type", "Downgrade")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Unauthorised Operation Downgrade")))
                .andDo(print());
        user.setAuthLevel("SUPER");
        assertEquals("SUPER", user.getAuthLevel());
    }

    @Test
    public void alter2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/alter")
                        .param("username", "UserThree")
                        .param("type", "Test")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Unknown Operation Test")))
                .andDo(print());
    }

    @Test
    public void alter3() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/alter")
                        .param("username", "UserTwo")
                        .param("type", "Promote")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Cannot Promote Admin User")))
                .andDo(print());
    }

    @Test
    public void alter4() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/alter")
                        .param("username", "UserThree")
                        .param("type", "Downgrade")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Cannot Downgrade Guest User")))
                .andDo(print());
    }

    @Test
    public void alter5() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/alter")
                        .param("username", "UserOne")
                        .param("type", "Promote")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Cannot Modify Super User")))
                .andDo(print());
    }

    @Test
    public void alter6() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/alter")
                        .param("username", "UserOne")
                        .param("type", "Downgrade")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Cannot Modify Super User")))
                .andDo(print());
    }

    @Test
    public void alter7() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/alter")
                        .param("username", "User")
                        .param("type", "Promote")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("No Username Matched")))
                .andDo(print());
    }

    @Test
    public void alter8() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/alter")
                        .param("username", "UserTwo")
                        .param("type", "Downgrade")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("1")))
                .andDo(print());
    }

    @Test
    public void alter9() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/alter")
                        .param("username", "UserThree")
                        .param("type", "Promote")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("1")))
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
        mvc.perform(MockMvcRequestBuilders.get("/user/centre")
                        .session(session))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void centre2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/centre"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void centre3() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/centre")
                        .session(session))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void upload0() throws Exception {
        String content = CryptoUtils.base64Encode(Files.readAllBytes(Paths.get(classpath + "/assets/img/avatar/default.jpg")));
        mvc.perform(MockMvcRequestBuilders.post("/user/upload")
                        .param("content", content)
                        .param("type", ".jpeg")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Please Select JPG or PNG Image")))
                .andDo(print());
    }

    @Test
    public void upload1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/upload")
                        .param("content", "")
                        .param("type", ".jpg")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Avatar Image Is Empty")))
                .andDo(print());
    }

    @Test
    public void upload2() {
        // TODO
    }

    @Test
    public void change0() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/change")
                        .param("username", "UserOne")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Username Does Not Change")))
                .andDo(print());
    }

    @Test
    public void change1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/change")
                        .param("username", "UserTwo")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Duplicate Username")))
                .andDo(print());
    }

    @Test
    public void change2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/change")
                        .param("username", "")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Empty Username")))
                .andDo(print());
    }

    @Test
    public void change3() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/change")
                        .param("username", "User")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("1")))
                .andDo(print());
        assertEquals("User", user.getUsername());
    }

    @Test
    public void update0() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/update")
                        .param("old_password", password)
                        .param("password", "Pass")
                        .param("confirm_password", CryptoUtils.toHex(CryptoUtils.hash("Pass", "MD5")))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Password Is Too Short")))
                .andDo(print());
    }

    @Test
    public void update1() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/update")
                        .param("old_password", password)
                        .param("password", "PasswordPasswordPassword")
                        .param("confirm_password", CryptoUtils.toHex(CryptoUtils.hash("PasswordPasswordPassword", "MD5")))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Password Is Too Long")))
                .andDo(print());
    }

    @Test
    public void update2() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/update")
                        .param("old_password", "Password")
                        .param("password", "password")
                        .param("confirm_password", CryptoUtils.toHex(CryptoUtils.hash("password", "MD5")))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Origin Password Is Incorrect")))
                .andDo(print());
    }

    @Test
    public void update3() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/update")
                        .param("old_password", password)
                        .param("password", "Password")
                        .param("confirm_password", password)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Password Does Not Change")))
                .andDo(print());
    }

    @Test
    public void update4() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/update")
                        .param("old_password", password)
                        .param("password", "password")
                        .param("confirm_password", password)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("Confirm Password Is Unmatched")))
                .andDo(print());
    }

    @Test
    public void update5() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/user/update")
                        .param("old_password", password)
                        .param("password", "password")
                        .param("confirm_password", CryptoUtils.toHex(CryptoUtils.hash("password", "MD5")))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(new StringContains("1")))
                .andDo(print());
    }

}
