package basic.mvc.demo.example.action;

import basic.mvc.demo.example.service.ExampleService;
import basic.mvc.utility.BaseController;
import basic.mvc.utility.PageList;
import basic.mvc.utility.Record;
import basic.mvc.utility.exception.ParameterUnexpectedException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RestController
@RequestMapping(value = "/paginate-merge")
public class MergeController extends BaseController {

    private final ExampleService exampleService;

    @Autowired
    public MergeController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping(value = "/index")
    public ModelAndView index() {
        this.init();
        logger.info("Paginate Merge Demo");
        return new ModelAndView("example/merge");
    }

    @PostMapping(value = "/load")
    public void load(@RequestParam int page, @RequestParam int rows) {
        try {
            PageList<Record> exampleList = exampleService.findPage("SELECT ID, NAME, TYPE, DESCRIPTION FROM EXAMPLE ORDER BY NAME", page, rows);
            logger.info("Current Example List: " + exampleList);
            long totalPage = exampleList.getPageTotal();
            long totalSize = exampleList.getSizeTotal();
            PageList<Record> finalList;
            if("1".equals(this.getPara("mode", "0"))) {
                List<Record> innerList = exampleList.getList();
                if(innerList.size() < rows) {
                    PageList<Record> userList = innerList.isEmpty() ? exampleService.findPage("SELECT * FROM (SELECT ROWNUM NO, ID, USERNAME AS NAME, AUTH_LEVEL AS TYPE, PASSWORD AS DESCRIPTION FROM SYS_USER) T WHERE T.NO > " + (rows * totalPage - totalSize) + " ORDER BY NAME", page - (int)totalPage, rows) : exampleService.findPage("SELECT ID, USERNAME AS NAME, AUTH_LEVEL AS TYPE, PASSWORD AS DESCRIPTION FROM SYS_USER ORDER BY USERNAME", 1, rows - innerList.size());
                    logger.info("Current User List: " + userList);
                    innerList.addAll(userList.getList());
                }
                List<Map<String, Object>> listMap = exampleService.findList("SELECT ID FROM SYS_USER");
                totalSize += listMap.size();
                finalList = new PageList<>(page, rows, (int)(totalSize / rows + (totalSize % rows == 0 ? 0 : 1)), totalSize, innerList);
                logger.info("Merged Final List: " + exampleList);
            } else {
                finalList = exampleList;
            }
            JSONObject json = finalList.toJsonGrid();
            this.renderJson(json.toString());
        } catch (ParameterUnexpectedException e) {
            logger.error("Failed to Query Example Information: ", e);
            this.ajaxDoneFailure("Unexpected Parameters");
        } catch (Exception e) {
            logger.error("Failed to Query Example Information: ", e);
        }
    }

}
