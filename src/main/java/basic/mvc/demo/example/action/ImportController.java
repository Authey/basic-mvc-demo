package basic.mvc.demo.example.action;

import basic.mvc.demo.example.service.ExampleService;
import basic.mvc.utility.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "/data-import")
public class ImportController extends BaseController {

    private final ExampleService exampleService;

    @Autowired
    public ImportController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @RequestMapping(value = "/index", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView index() {
        this.init();
        logger.info("Data Import Demo");
        return new ModelAndView("example/import");
    }

}
