package basic.mvc.demo.example.action;

import basic.mvc.demo.example.service.ExampleService;
import basic.mvc.utility.BaseController;
import basic.mvc.utility.PageList;
import basic.mvc.utility.Record;
import basic.mvc.utility.exception.ParameterUnexpectedException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

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

    @PostMapping(value = "/load")
    public void load(@RequestParam int page, @RequestParam int rows) {
        try {
            PageList<Record> exampleList = exampleService.findPage("SELECT ID, NAME, TYPE, FLAG, SORT_ORDER FROM EXAMPLE ORDER BY NAME", page, rows);
            logger.info("Example List: " + exampleList);
            JSONObject json = exampleList.toJsonGrid();
            if("1".equals(this.getPara("mode", "0"))) {
                JSONArray array = json.getJSONArray("rows");
                logger.info("Pre-Sort Array: " + array);
                array.sort(Comparator.comparing(obj -> ((JSONObject) obj).getJSONObject("cell").getInteger("SORT_ORDER")));
                logger.info("Post-Sort Array: " + array);
                json.put("rows", array);
            }
            this.renderJson(json.toString());
        } catch (ParameterUnexpectedException e) {
            logger.error("Failed to Query Example Information: ", e);
            this.ajaxDoneFailure("Unexpected Parameters");
        } catch (Exception e) {
            logger.error("Failed to Query Example Information: ", e);
        }
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
                params.add(id + ":" + RandomStringUtils.randomAlphabetic(100));
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
