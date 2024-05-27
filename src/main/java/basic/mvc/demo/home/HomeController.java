package basic.mvc.demo.home;

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
        return new ModelAndView("nav".equals(constant.getProperty("view.model", "nav")) ? "redirect:/index" : "redirect:/home");
    }

    @GetMapping(value = "/home")
    public ModelAndView home() {
        this.setAttr("root", this.getRootPath());
        logger.info("Integrated Home Page Request");
        return new ModelAndView("home");
    }

    @RequestMapping(value = "/index", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index() {
        this.init();
        logger.info("Home Page Request");
        User user = this.getUser();
        if (user != null) {
            this.setAttr("user", user);
        }
        return new ModelAndView("home/index");
    }

}
