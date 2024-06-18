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

}
