package basic.mvc.demo.user.action;

import basic.mvc.demo.user.service.UserService;
import basic.mvc.utility.BaseController;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/user")
public class UserController extends BaseController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        logger.info("Accessing User Page");
        this.setAttr("root", this.getRootPath());
        return "user/index";
    }

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public void query() {
        try {
            List<Map<String, Object>> userList = userService.find("SELECT ID, USER_NAME, AUTH_LEVEL FROM SYS_USER");
            logger.info("User List: " + userList);
            JSONArray json = JSONArray.fromObject(userList);
            this.renderJson(json.toString());
        } catch (Exception e) {
            logger.error("Cannot Query User Information");
        }
    }

}
