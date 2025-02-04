package basic.mvc.demo.user.action;

import basic.mvc.demo.user.po.User;
import basic.mvc.demo.user.service.UserService;
import basic.mvc.utility.BaseController;
import basic.mvc.utility.CryptoUtils;
import basic.mvc.utility.PageList;
import basic.mvc.utility.Record;
import basic.mvc.utility.exception.ParameterUnexpectedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

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
        this.init();
        String type = this.getPara("type", "Login");
        User user = this.getUser();
        if (!"Login".equals(type) && !"Enroll".equals(type) && !"Manage".equals(type) && !"Logout".equals(type)) {
            logger.warn("Unknown Request Type: " + type);
            return new ModelAndView("redirect:/status/404");
        } else if (user == null && ("Manage".equals(type) || "Logout".equals(type))) {
            logger.warn("Unauthorised Request Type: " + type);
            type = "Login";
        } else if (user != null && ("Login".equals(type) || "Enroll".equals(type))) { // Url Modified in Navbar/Embedded View Redirect to Index/Base Request
            return new ModelAndView("redirect:/");
        } else if (user != null && "Manage".equals(type)) {
            this.setAttr("auth", user.getAuthLevel());
        }
        logger.info("User " + type + " Request");
        this.setAttr("type", type);
        return new ModelAndView("user/index");
    }

    @PostMapping(value = "/login")
    public void login(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userService.findObject("SELECT ID, USERNAME, PASSWORD, AUTH_LEVEL, AVATAR_TYPE FROM SYS_USER WHERE USERNAME = ?", username);
            boolean match = password.equalsIgnoreCase(user.getPassword());
            logger.info(match ? "User Information Matched" : "User Information Unmatched");
            if (match) {
                this.setUser(user);
                String avatarType = user.getAvatarType();
                this.setSession("avatar", StringUtils.isNotBlank(avatarType) ? "static/img/avatar/" + user.getId() + avatarType : "static/img/avatar/default.jpg");
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
                logger.error("Failed to Insert User Information: Password Too Short");
                this.ajaxDoneFailure("Password Is Too Short");
            } else if (password.length() > 20) {
                logger.error("Failed to Insert User Information: Password Too Long");
                this.ajaxDoneFailure("Password Is Too Long");
            } else {
                List<Object> params = new ArrayList<>();
                params.add(UUID.randomUUID().toString().toUpperCase());
                params.add(username);
                params.add(CryptoUtils.hash(password, "MD5"));
                params.add("GUEST");
                int row = userService.update("INSERT INTO SYS_USER (ID, USERNAME, PASSWORD, AUTH_LEVEL) VALUES (?, ?, ?, ?)", params.toArray());
                logger.info("Succeeded to Insert User " + username);
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
            String username = this.getPara("username", "");
            PageList<Record> userList = userService.findPage("SELECT ID, USERNAME, PASSWORD, AUTH_LEVEL FROM SYS_USER WHERE USERNAME LIKE ? ESCAPE '/' ORDER BY USERNAME", page, rows, "%" + username + "%");
            logger.info("User List: " + userList);
            this.renderJson(userList.toJsonGrid().toString());
        } catch (ParameterUnexpectedException e) {
            logger.error("Failed to Query User Information: ", e);
            this.ajaxDoneFailure("Unexpected Parameters");
        } catch (Exception e) {
            logger.error("Failed to Query User Information: ", e);
        }
    }

    @PostMapping(value = "/remove")
    public void remove(@RequestParam String username) {
        if ("GUEST".equals(this.getUser().getAuthLevel())) {
            logger.error("Failed to Delete User " + username + ": Unauthorised Deletion");
            this.ajaxDoneFailure("Unauthorised Deletion");
        } else {
            try {
                String authLevel = userService.findObject("SELECT AUTH_LEVEL FROM SYS_USER WHERE USERNAME = ?", username).getAuthLevel();
                if (!"GUEST".equals(authLevel)) {
                    logger.error("Failed to Delete User " + username + ": Delete Admin Or Super Not Allowed");
                    this.ajaxDoneFailure("Cannot Delete Admin Or Super User");
                } else {
                    int row = userService.update("DELETE FROM SYS_USER WHERE USERNAME = ?", username);
                    logger.info("Succeeded to Delete User " + username);
                    this.ajaxDoneSuccess(Integer.toString(row));
                }
            } catch (EmptyResultDataAccessException e) {
                logger.error("Failed to Delete User " + username +": ", e);
                this.ajaxDoneFailure("No Username Matched");
            } catch (Exception e) {
                logger.error("Failed to Delete User " + username +": ", e);
                this.ajaxDoneFailure(null);
            }
        }
    }

    @PostMapping(value = "/alter")
    public void alter(@RequestParam String username, @RequestParam String type) {
        if (!"SUPER".equals(this.getUser().getAuthLevel())) {
            logger.error("Failed to " + type + " User " + username + ": Unauthorised Operation");
            this.ajaxDoneFailure("Unauthorised Operation " + type);
        } else if (!"Promote".equalsIgnoreCase(type) && !"Downgrade".equalsIgnoreCase(type)) {
            logger.error("Failed to " + type + " User " + username + ": Unknown Operation");
            this.ajaxDoneFailure("Unknown Operation " + type);
        } else {
            try {
                String authLevel = userService.findObject("SELECT AUTH_LEVEL FROM SYS_USER WHERE USERNAME = ?", username).getAuthLevel();
                if ("ADMIN".equals(authLevel) && "Promote".equalsIgnoreCase(type)) {
                    logger.error("Failed to " + type + " User " + username + ": Promote Admin Not Allowed");
                    this.ajaxDoneFailure("Cannot Promote Admin User");
                } else if ("GUEST".equals(authLevel) && "Downgrade".equalsIgnoreCase(type)) {
                    logger.error("Failed to " + type + " User " + username + ": Downgrade Guest Not Allowed");
                    this.ajaxDoneFailure("Cannot Downgrade Guest User");
                } else if ("SUPER".equals(authLevel)) {
                    logger.error("Failed to " + type + " User " + username + ": Modify Super Not Allowed");
                    this.ajaxDoneFailure("Cannot Modify Super User");
                } else {
                    int row = userService.update("UPDATE SYS_USER SET AUTH_LEVEL = ? WHERE USERNAME = ?", "Promote".equalsIgnoreCase(type) ? "ADMIN" : "GUEST", username);
                    logger.info("Succeeded to " + type + " User " + username);
                    this.ajaxDoneSuccess(Integer.toString(row));
                }
            } catch (EmptyResultDataAccessException e) {
                logger.error("Failed to " + type + " User: ", e);
                this.ajaxDoneFailure("No Username Matched");
            } catch (Exception e) {
                logger.error("Failed to " + type + " User: ", e);
                this.ajaxDoneFailure(null);
            }
        }
    }

    @RequestMapping(value = "/centre", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView centre() {
        this.init();
        User user = this.getUser();
        if (user == null) {
            logger.warn("Unauthorised Centre Access");
            return new ModelAndView("user/centre");
        }
        logger.info(user.getUsername() + " Centre Accessing");
        return new ModelAndView("user/centre");
    }

    @PostMapping(value = "/upload")
    public void upload(@RequestParam String content, @RequestParam String type) {
        try {
            User user = this.getUser();
            String userId = user.getId();
            if (!".jpg".equals(type) && !".png".equals(type)) {
                logger.error("Failed to Upload Avatar: Image Type Incorrect");
                this.ajaxDoneFailure("Please Select JPG or PNG Image");
            } else if (StringUtils.isBlank(content)) {
                logger.error("Failed to Upload Avatar: Empty Avatar Image");
                this.ajaxDoneFailure("Avatar Image Is Empty");
            } else {
                String classpath = ResourceUtils.getURL("classpath:").getPath();
                File avatarImg = new File( classpath + "assets/img/avatar/" + userId + type);
                if(!avatarImg.exists()) {
                    boolean fr = avatarImg.createNewFile();
                    if(!fr) {
                        logger.error("Failed to Upload Avatar: Save Avatar Image Failed");
                        this.ajaxDoneFailure("Failed to Save Avatar Image");
                        return;
                    }
                }
                File avatarImgBackup = new File(classpath.replace("webapp/WEB-INF/classes", "resources") + "assets/img/avatar/" + userId + type);
                if(!avatarImgBackup.exists()) {
                    boolean fr = avatarImgBackup.createNewFile();
                    if(!fr) {
                        logger.error("Failed to Upload Avatar: Save Avatar Image Failed");
                        this.ajaxDoneFailure("Failed to Save Avatar Image");
                        return;
                    }
                }
                FileOutputStream fos = new FileOutputStream(avatarImg);
                fos.write(CryptoUtils.base64Decode(content)); fos.close();
                FileOutputStream fosBackup = new FileOutputStream(avatarImgBackup);
                fosBackup.write(CryptoUtils.base64Decode(content)); fosBackup.close();
                int row = userService.update("UPDATE SYS_USER SET AVATAR_TYPE = ? WHERE ID = ?", type, userId);
                if (row == 1) {
                    user.setAvatarType(type);
                    this.setSession("avatar", "static/img/avatar/" + userId + type);
                    logger.info("Succeeded to Upload Avatar: " + userId + type);
                    this.ajaxDoneSuccess(Integer.toString(row));
                } else {
                    logger.error("Failed to Upload Avatar: Update Command Error");
                    this.ajaxDoneFailure("Unexpected SQL Error");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to Upload Avatar: ", e);
            this.ajaxDoneFailure(null);
        }
    }

    @PostMapping(value = "/change")
    public void change(@RequestParam String username) {
        try {
            User user = this.getUser();
            if (user.getUsername().equals(username)) {
                logger.error("Failed to Change Username: Username No Change");
                this.ajaxDoneFailure("Username Does Not Change");
            } else {
                int row = userService.update("UPDATE SYS_USER SET USERNAME = ? WHERE ID = ?", username, user.getId());
                if (row == 1) {
                    this.getUser().setUsername(username);
                    logger.info("Succeeded to Change Username " + username);
                    this.ajaxDoneSuccess(Integer.toString(row));
                } else {
                    logger.error("Failed to Change Username: Update Command Error");
                    this.ajaxDoneFailure("Unexpected SQL Error");
                }
            }
        } catch (DuplicateKeyException e) {
            logger.error("Failed to Change Username: ", e);
            this.ajaxDoneFailure("Duplicate Username");
        } catch (UncategorizedSQLException e) {
            logger.error("Failed to Change Username: ", e);
            this.ajaxDoneFailure("Empty Username");
        } catch (Exception e) {
            logger.error("Failed to Change Username: ", e);
            this.ajaxDoneFailure(null);
        }
    }

    @PostMapping(value = "/update")
    public void update(@RequestParam String old_password, @RequestParam String password, @RequestParam String confirm_password) {
        try {
            User user = this.getUser();
            String forePwd = user.getPassword();
            String hashPwd = CryptoUtils.toHex(CryptoUtils.hash(password, "MD5")).toUpperCase();
            if (password.length() <= 6) {
                logger.error("Failed to Update Password: Password Too Short");
                this.ajaxDoneFailure("Password Is Too Short");
            } else if (password.length() > 20) {
                logger.error("Failed to Update Password: Password Too Long");
                this.ajaxDoneFailure("Password Is Too Long");
            } else if (!forePwd.equalsIgnoreCase(old_password)) {
                logger.error("Failed to Update Password: Password Incorrect");
                this.ajaxDoneFailure("Origin Password Is Incorrect");
            } else if (forePwd.equalsIgnoreCase(hashPwd)) {
                logger.error("Failed to Update Password: Password No Change");
                this.ajaxDoneFailure("Password Does Not Change");
            } else if (!hashPwd.equalsIgnoreCase(confirm_password)) {
                logger.error("Failed to Update Password: Password Unmatched");
                this.ajaxDoneFailure("Confirm Password Is Unmatched");
            } else {
                int row = userService.update("UPDATE SYS_USER SET PASSWORD = ? WHERE ID = ?", hashPwd, user.getId());
                if (row == 1) {
                    this.getUser().setPassword(hashPwd);
                    logger.info("Succeeded to Update Password " + hashPwd);
                    this.ajaxDoneSuccess(Integer.toString(row));
                } else {
                    logger.error("Failed to Update Password: Update Command Error");
                    this.ajaxDoneFailure("Unexpected SQL Error");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to Update Password: ", e);
            this.ajaxDoneFailure(null);
        }
    }

}
