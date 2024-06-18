package basic.mvc.demo.example.action;

import basic.mvc.demo.example.service.ExampleService;
import basic.mvc.utility.BaseController;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping(value = "/example-base")
public class ExampleBaseController extends BaseController {

    private final ExampleService exampleService;

    @Autowired
    public ExampleBaseController(ExampleService exampleService) {
        this.exampleService = exampleService;
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
                params.add(new Random().nextInt(20));
                int row = exampleService.update("INSERT INTO EXAMPLE (ID, NAME, TYPE, FLAG, DESCRIPTION, SORT_ORDER) VALUES (?, ?, ?, ?, ?, ?)", params.toArray());
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

}
