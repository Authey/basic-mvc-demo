package basic.mvc.demo.example.action;

import basic.mvc.demo.example.service.ExampleService;
import basic.mvc.utility.BaseController;
import org.apache.commons.lang.RandomStringUtils;
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

    @PostMapping(value = "/fill")
    public void fill() {
        if (!"SUPER".equals(this.getUser().getAuthLevel())) {
            logger.error("Failed to Failed to Fill Data: Unauthorised Fill");
            this.ajaxDoneFailure("Unauthorised Fill");
        } else {
            try {
                List<Object> params = new ArrayList<>();
                String id = UUID.randomUUID().toString().toUpperCase();
                params.add(id);
                params.add(RandomStringUtils.randomAlphabetic(10).toUpperCase());
                params.add("Random");
                params.add("0");
                params.add(id + ":" + RandomStringUtils.randomAlphabetic(20));
                int row = exampleService.update("INSERT INTO EXAMPLE (ID, NAME, TYPE, FLAG, DESCRIPTION) VALUES (?, ?, ?, ?, ?)", params.toArray());
                logger.info("Succeeded to Fill Data");
                this.ajaxDoneSuccess(Integer.toString(row));
            } catch (Exception e) {
                logger.error("Failed to Fill Data: ", e);
                this.ajaxDoneFailure(null);
            }
        }
    }

    @PostMapping(value = "/clear")
    public void clear() {
        if (!"SUPER".equals(this.getUser().getAuthLevel())) {
            logger.error("Failed to Failed to Clear Data: Unauthorised Clearance");
            this.ajaxDoneFailure("Unauthorised Clearance");
        } else {
            try {
                int row = exampleService.update("DELETE FROM EXAMPLE WHERE TYPE = ?", "Random");
                logger.info("Succeeded to Clear Data");
                this.ajaxDoneSuccess(Integer.toString(row));
            } catch (Exception e) {
                logger.error("Failed to Clear Data: ", e);
                this.ajaxDoneFailure(null);
            }
        }
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
