package basic.mvc.demo.home.action;

import basic.mvc.demo.user.po.User;
import basic.mvc.utility.BaseController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "")
public class HomeController extends BaseController {

    @GetMapping(value = "/")
    public ModelAndView base() {
        logger.info("Base URL Request");
        return new ModelAndView("redirect:/home");
    }

    @RequestMapping(value = "/home", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView home() {
        this.setAttr("root", this.getRootPath());
        logger.info("Home Page Request");
        User user = this.getUser();
        if (user != null) {
            this.setAttr("user", user);
        }
        this.setAttr("alert", this.getPara("alert", null));
        this.setAttr("tag", "home");
        this.setAttr("title", "Home");
        return new ModelAndView("base/home");
    }

}
