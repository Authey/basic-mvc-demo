package basic.mvc.demo.user.action;

import basic.mvc.demo.user.po.User;
import basic.mvc.demo.user.service.UserService;
import basic.mvc.utility.BaseController;
import basic.mvc.utility.CryptoUtils;
import basic.mvc.utility.PageList;
import basic.mvc.utility.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/user")
public class UserController extends BaseController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/index", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index() {
        this.setAttr("root", this.getRootPath());
        String type = this.getPara("type", "Login");
        User user = this.getUser();
        if (user == null && ("Logout".equals(type) || "Manage".equals(type))) {
            logger.warn("Unauthorised Request Type: " + type);
            type = "Login";
        } else if (user != null) {
            this.setAttr("auth", user.getAuthLevel());
        }
        logger.info("User " + type + " Request");
        this.setAttr("type", type);
        this.setAttr("alert", this.getPara("alert", null));
        this.setAttr("tag", "user");
        this.setAttr("title", type);
        return new ModelAndView("user/index");
    }

    @PostMapping(value = "/login")
    public void login(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userService.findObject("SELECT USERNAME, PASSWORD, AUTH_LEVEL FROM SYS_USER WHERE USERNAME = ?", username);
            boolean match = CryptoUtils.toHex(CryptoUtils.hash(password, "MD5")).equalsIgnoreCase(user.getPassword());
            logger.info(match ? "User Information Matched" : "User Information Unmatched");
            if (match) {
                this.setUser(user);
                this.ajaxDoneSuccess(null);
            } else {
                this.ajaxDoneFailure("Password Unmatched");
            }
        } catch (EmptyResultDataAccessException e) {
            logger.error("Failed to Perform User Information Comparison: ", e);
            this.ajaxDoneFailure("No User Found");
        } catch (Exception e) {
            logger.error("Failed to Perform User Information Comparison: ", e);
            this.ajaxDoneFailure(null);
        }
    }

    @PostMapping(value = "/logout")
    public void logout() {
        request.getSession().removeAttribute("user");
    }

    @PostMapping(value = "/enroll")
    public void enroll(@RequestParam String username, @RequestParam String password) {
        try {
            if (password.length() <= 6) {
                logger.error("Failed to Insert User Information");
                this.ajaxDoneFailure("Password Is Too Short");
            } else {
                List<Object> params = new ArrayList<>();
                params.add(UUID.randomUUID().toString().toUpperCase());
                params.add(username);
                params.add(CryptoUtils.hash(password, "MD5"));
                params.add("NORMAL");
                int row = userService.update("INSERT INTO SYS_USER (ID, USERNAME, PASSWORD, AUTH_LEVEL) VALUES (?, ?, ?, ?)", params.toArray());
                logger.info("Succeeded to Insert User Information");
                this.ajaxDoneSuccess(Integer.toString(row));
            }
        } catch (DuplicateKeyException e) {
            logger.error("Failed to Insert User Information: ", e);
            this.ajaxDoneFailure("Duplicate Username");
        } catch (DataIntegrityViolationException e) {
            logger.error("Failed to Insert User Information: ", e);
            this.ajaxDoneFailure("Empty Username");
        } catch (Exception e) {
            logger.error("Failed to Insert User Information: ", e);
            this.ajaxDoneFailure(null);
        }
    }

    @PostMapping(value = "/load")
    public void load(@RequestParam int page, @RequestParam int rows) {
        try {
            PageList<Record> userList = userService.findPage("SELECT ID, USERNAME, PASSWORD, AUTH_LEVEL FROM SYS_USER", page, rows);
            logger.info("User List: " + userList);
            this.renderJson(userList.toJsonGrid().toString());
        } catch (Exception e) {
            logger.error("Failed to Query User Information: ", e);
        }
    }

    @PostMapping(value = "/remove")
    public void remove(@RequestParam String username) {
        try {
            int row = userService.update("DELETE FROM SYS_USER WHERE USERNAME = ?", username);
            logger.info("Succeeded to Delete User Information");
            this.ajaxDoneSuccess(Integer.toString(row));
        } catch (Exception e) {
            logger.error("Failed to Delete User Information: ", e);
            this.ajaxDoneFailure(null);
        }
    }

}
