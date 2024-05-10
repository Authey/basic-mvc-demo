package basic.mvc.demo.home.action;

import basic.mvc.utility.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "")
public class HomeController extends BaseController {

    @GetMapping(value = "/")
    public ModelAndView base() {
        logger.info("Base URL Request");
        return new ModelAndView("redirect:/home");
    }

    @GetMapping(value = "/home")
    public ModelAndView home() {
        logger.info("Accessing Home Page");
        this.setAttr("root", this.getRootPath());
        return new ModelAndView("home");
    }

}
