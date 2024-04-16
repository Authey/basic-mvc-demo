package basic.web.demo.home.action;

import basic.web.demo.BaseController;
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
        return "redirect:/home";
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(HttpServletRequest request, HttpServletResponse response) {
        return "home";
    }

}
