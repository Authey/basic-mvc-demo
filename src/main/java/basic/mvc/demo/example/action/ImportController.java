package basic.mvc.demo.example.action;

import basic.mvc.demo.example.service.ExampleService;
import basic.mvc.demo.user.po.User;
import basic.mvc.utility.BaseController;
import basic.mvc.utility.CryptoUtils;
import basic.mvc.utility.PageList;
import basic.mvc.utility.Record;
import basic.mvc.utility.exception.NoSuchElementFoundException;
import basic.mvc.utility.exception.ParameterUnexpectedException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/data-import")
public class ImportController extends BaseController {

    private final ExampleService exampleService;

    @Autowired
    public ImportController(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    @GetMapping(value = "/index")
    public ModelAndView index() {
        this.init();
        logger.info("Data Import Demo");
        return new ModelAndView("example/import");
    }

    @PostMapping(value = "/load")
    public void load(@RequestParam int page, @RequestParam int rows) {
        try {
            PageList<Record> exampleList = exampleService.findPage("SELECT ID, NAME, TYPE, FLAG, DESCRIPTION FROM EXAMPLE ORDER BY NAME", page, rows);
            logger.info("Example List: " + exampleList);
            this.renderJson(exampleList.toJsonGrid().toString());
        } catch (ParameterUnexpectedException e) {
            logger.error("Failed to Query Example Information: ", e);
            this.ajaxDoneFailure("Unexpected Parameters");
        } catch (Exception e) {
            logger.error("Failed to Query Example Information: ", e);
        }
    }

    @PostMapping(value = "/clear")
    public void clear() {
        if (!"SUPER".equals(this.getUser().getAuthLevel())) {
            logger.error("Failed to Failed to Clear Data: Unauthorised Clearance");
            this.ajaxDoneFailure("Unauthorised Clearance");
        } else {
            try {
                int row = exampleService.update("DELETE FROM EXAMPLE");
                logger.info("Succeeded to Clear Data");
                this.ajaxDoneSuccess(Integer.toString(row));
            } catch (Exception e) {
                logger.error("Failed to Clear Data: ", e);
                this.ajaxDoneFailure(null);
            }
        }
    }

    @PostMapping(value = "/upload")
    public void upload(@RequestParam String content, @RequestParam String type) {
        try {
            if (!".xls".equals(type) && !".xlsx".equals(type)) {
                logger.error("Failed to Upload Excel Data: File Type Incorrect");
                this.ajaxDoneFailure("Please Select XLS or XLSX File");
            } else if (StringUtils.isBlank(content)) {
                logger.error("Failed to Upload Excel Data: Empty Content");
                this.ajaxDoneFailure("Excel Content Is Empty");
            } else {
                // TODO
            }
        } catch (Exception e) {
            logger.error("Failed to Upload Excel Data: ", e);
            this.ajaxDoneFailure(null);
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
                int row = exampleService.update("INSERT INTO EXAMPLE (ID, NAME, TYPE, FLAG, DESCRIPTION) VALUES (?, ?, ?, ?, ?)", params.toArray());
                logger.info("Succeeded to Fill Data");
                this.ajaxDoneSuccess(Integer.toString(row));
            } catch (Exception e) {
                logger.error("Failed to Fill Data: ", e);
                this.ajaxDoneFailure(null);
            }
        }
    }

    @GetMapping(value = "/download")
    public void download() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("assets/template/data_import_template.xlsx");
        if (inputStream != null) {
            logger.info("Load Template Resource Successfully");
            try {
                XSSFWorkbook wb = new XSSFWorkbook(inputStream);
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;" + "filename=" + new String("data_import_template.xlsx".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
                ServletOutputStream localServletOutputStream = response.getOutputStream();
                wb.write(localServletOutputStream);
                localServletOutputStream.flush();
                localServletOutputStream.close();
                logger.info("Template File Is Downloaded");
            } catch (Exception e) {
                logger.error("Failed to Download Excel Template: ", e);
                this.ajaxDoneFailure(null);
            }
        } else {
            logger.error("Failed to Download Excel Template: No Template File Found");
            this.ajaxDoneFailure("Template File Is Not Found");
        }
    }

}
