package basic.mvc.demo.example.action;

import basic.mvc.demo.example.service.ExampleService;
import basic.mvc.utility.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "/list-sort")
public class SortController extends BaseController {

    private final ExampleService exampleService;

    @Autowired
    public SortController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping(value = "/index")
    public ModelAndView index() {
        this.init();
        logger.info("List Sort Demo");
        return new ModelAndView("example/sort");
    }

}
