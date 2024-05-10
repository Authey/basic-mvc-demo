package basic.mvc.demo.user.action;

import basic.mvc.demo.user.service.UserService;
import basic.mvc.utility.BaseController;
import basic.mvc.utility.CryptoUtils;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/user")
public class UserController extends BaseController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/index")
    public ModelAndView index() {
        logger.info("Accessing User Page");
        this.setAttr("root", this.getRootPath());
        return new ModelAndView("user/index");
    }

    @PostMapping(value = "/query")
    public void query() {
        try {
            List<Map<String, Object>> userList = userService.find("SELECT ID, USERNAME, PASSWORD, AUTH_LEVEL FROM SYS_USER");
            logger.info("User List: " + userList);
            JSONArray json = JSONArray.fromObject(userList);
            this.renderJson(json.toString());
        } catch (Exception e) {
            logger.error("Failed to Query User Information: ", e);
        }
    }

    @PostMapping(value = "/add")
    public void add() {
        try {
            List<Object> params = new ArrayList<>();
            params.add(UUID.randomUUID().toString().toUpperCase());
            params.add("Authey");
            params.add(CryptoUtils.hash("root", "MD5"));
            params.add("ALL");
            userService.update("INSERT INTO SYS_USER (ID, USERNAME, PASSWORD, AUTH_LEVEL) VALUES (?, ?, ?, ?)", params.toArray());
            logger.info("Succeeded to Insert User Information");
            this.ajaxDoneSuccess(null);
        } catch (Exception e) {
            logger.error("Failed to Insert User Information: ", e);
            this.ajaxDoneFailure(null);
        }
    }

    @PostMapping(value = "/remove")
    public void remove() {
        try {
            userService.update("DELETE FROM SYS_USER");
            logger.info("Succeeded to Delete User Information");
            this.ajaxDoneSuccess(null);
        } catch (Exception e) {
            logger.error("Failed to Delete User Information: ", e);
            this.ajaxDoneFailure(null);
        }
    }

}
