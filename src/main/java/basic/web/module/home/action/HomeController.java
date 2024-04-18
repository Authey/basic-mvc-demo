package basic.web.module.home.action;

import basic.web.utility.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("")
public class HomeController extends BaseController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Base URL Request");
        return "redirect:/home";
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Home URL Request");
        return "home";
    }

}
