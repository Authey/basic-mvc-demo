package basic.mvc.demo.status;

import basic.mvc.utility.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "/status")
public class StatusController extends BaseController {

    @GetMapping(value = "/{code}")
    public ModelAndView show(@PathVariable("code") String code) {
        this.setAttr("root", this.getRootPath());
        String model = constant.getProperty("view.model", "nav");
        this.setAttr("view", model);
        return new ModelAndView("status/" + code);
    }

}
