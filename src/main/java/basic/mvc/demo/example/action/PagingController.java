package basic.mvc.demo.example.action;

import basic.mvc.demo.example.service.ExampleService;
import basic.mvc.utility.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RestController
@RequestMapping(value = "/table-paging")
public class PagingController extends BaseController {

    private final ExampleService exampleService;

    @Autowired
    public PagingController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping(value = "/index")
    public ModelAndView index() {
        this.init();
        logger.info("Table Paging Demo");
        try {
            List<Map<String, Object>> exampleList = exampleService.findList("SELECT ID, NAME, TYPE, DESCRIPTION FROM EXAMPLE ORDER BY NAME");
            logger.info("Example List: " + exampleList);
            this.setAttr("exampleList", exampleList);
        } catch (Exception e) {
            logger.error("Failed to Query Example Information: ", e);
        }
        return new ModelAndView("example/paging");
    }

    @PostMapping(value = "/select")
    public void select(@RequestParam(value = "id_arr[]") String[] ids) {
        if (!"SUPER".equals(this.getUser().getAuthLevel())) {
            logger.error("Failed to Failed to Select Data: Unauthorised Selection");
            this.ajaxDoneFailure("Unauthorised Selection");
        } else {
            String id_str = Arrays.toString(ids);
            logger.info("Succeeded to Select IDs: " + id_str);
            this.ajaxDoneSuccess("Selected IDs: " + id_str);
        }
    }

}
