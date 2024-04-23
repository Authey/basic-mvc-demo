package basic.mvc.demo.home.action;

import basic.mvc.utility.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("")
public class HomeController extends BaseController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        logger.info("Base URL Request");
        return "redirect:/home";
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home() {
        logger.info("Home URL Request");
        return "home";
    }

}
