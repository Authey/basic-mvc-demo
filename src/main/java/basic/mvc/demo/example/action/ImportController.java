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
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
        File file = null;
        List<String> columnList = new ArrayList<>();
        int inserted = 0;
        try {
            if (!".xls".equals(type) && !".xlsx".equals(type)) {
                logger.error("Failed to Upload Excel Data: File Type Incorrect");
                this.ajaxDoneFailure("Please Select XLS or XLSX File");
            } else if (StringUtils.isBlank(content)) {
                logger.error("Failed to Upload Excel Data: Empty Content");
                this.ajaxDoneFailure("Excel Content Is Empty");
            } else {
                boolean is_xls = ".xls".equalsIgnoreCase(type);
                String classpath = ResourceUtils.getURL("classpath:").getPath();
                String filepath = classpath + "data-import" + type;
                file = new File(filepath);
                if(!file.exists()) {
                    boolean fr = file.createNewFile();
                    if(!fr) {
                        logger.error("Failed to Upload Excel Data: Save Excel File Failed");
                        this.ajaxDoneFailure("Failed to Save Excel File");
                        return;
                    }
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(CryptoUtils.base64Decode(content)); fos.close();
                Path path = Paths.get(filepath.substring(1));
                HSSFWorkbook hwb = null;
                XSSFWorkbook xwb = null;
                int sheetNum;
                if (is_xls) {
                    hwb = new HSSFWorkbook(Files.newInputStream(path));
                    sheetNum = hwb.getNumberOfSheets();
                } else {
                    xwb = new XSSFWorkbook(Files.newInputStream(path));
                    sheetNum = xwb.getNumberOfSheets();
                }
                logger.info("This Contains " + sheetNum + (sheetNum > 1 ? " Sheet Tables" : " Sheet Table"));
                for(int i = 0; i < sheetNum; i++) {
                    HSSFSheet hst = null;
                    XSSFSheet xst = null;
                    if (is_xls) {
                        hst = hwb.getSheetAt(i);
                        if (hst.getRow(0) == null) {
                            logger.info("Reading Sheet Table " + (i+1) + ": " + hst.getSheetName() + " Which Is Empty");
                            continue;
                        }
                        logger.info("Reading Sheet Table " + (i+1) + ": " + hst.getSheetName());
                    } else {
                        xst = xwb.getSheetAt(i);
                        if (xst.getRow(0) == null) {
                            logger.info("Reading Sheet Table " + (i+1) + ": " + xst.getSheetName() + " Which Is Empty");
                            continue;
                        }
                        logger.info("Reading Sheet Table " + (i+1) + ": " + xst.getSheetName());
                    }
                    StringBuilder insert = new StringBuilder("INSERT INTO EXAMPLE (ID");
                    StringBuilder v = new StringBuilder();
                    int maxRow;
                    int maxCol;
                    // Avoid Multiple Lines For Title - Jump Empty Lines After Title
                    int startRow = 1;
                    if (is_xls) {
                        maxRow = hst.getLastRowNum();
                        while (hst.getRow(startRow) == null || hst.getRow(startRow).getCell(0) == null || StringUtils.isBlank(hst.getRow(startRow).getCell(0).toString())) {
                            startRow += 1;
                        }
                        maxCol = hst.getRow(startRow).getLastCellNum();
                    } else {
                        maxRow = xst.getLastRowNum();
                        while (xst.getRow(startRow) == null || xst.getRow(startRow).getCell(0) == null || StringUtils.isBlank(xst.getRow(startRow).getCell(0).toString())) {
                            startRow += 1;
                        }
                        maxCol = xst.getRow(startRow).getLastCellNum();
                    }
                    ArrayList<String> keys = new ArrayList<>();
                    HashMap<String, String> rowData = new HashMap<>();
                    if(startRow < maxRow) {
                        for(int c = 0; c < maxCol; c++) {
                            String idx;
                            // Check If Current Row Or Column Has Value
                            if (is_xls) {
                                idx = hst.getRow(startRow) == null ? "" : hst.getRow(startRow).getCell(c) == null ? "" : hst.getRow(startRow).getCell(c).toString();
                            } else {
                                idx = xst.getRow(startRow) == null ? "" : xst.getRow(startRow).getCell(c) == null ? "" : xst.getRow(startRow).getCell(c).toString();
                            }
                            // Record Non-Empty Column Value
                            if (StringUtils.isNotBlank(idx)) {
                                keys.add(idx);
                                rowData.put(idx, "");
                            }
                            switch (idx) {
                                case "Name":
                                case "Type":
                                case "Flag":
                                    insert.append(", ").append(idx.toUpperCase());
                                    v.append(", ?");
                                    columnList.add(idx);
                                    break;
                                case "Order":
                                    insert.append(", SORT_ORDER");
                                    v.append(", ?");
                                    columnList.add(idx);
                                    break;
                                case "Description":
                                    insert.append(", DESCRIPTION");
                                    v.append(", #");
                                    columnList.add(idx);
                                    break;
                                default:
                                    break;
                            }
                        }
                        insert.append(") VALUES (?").append(v).append(")");
                        logger.info("Sheet Table " + (i+1) + " - Insert SQL Command: " + insert);
                        List<Object> param = new ArrayList<>();
                        param.add(UUID.randomUUID().toString().toUpperCase());
                        startRow += 1;
                        // Mark If One Data Handling Is Started
                        boolean start = false;
                        for(int r = startRow; r <= maxRow; r++) {
                            String firstCell;
                            if (is_xls) {
                                firstCell = hst.getRow(r) == null ? "" : hst.getRow(r).getCell(0) == null ? "" : hst.getRow(r).getCell(0).toString();
                            } else {
                                firstCell = xst.getRow(r) == null ? "" : xst.getRow(r).getCell(0) == null ? "" : xst.getRow(r).getCell(0).toString();
                            }
                            // In Case One Data Contains Multiple Merged Lines
                            if(!"".equals(firstCell)) {
                                // Check If One Data Handling Is Started
                                if(start) {
                                    StringBuilder clobBasis = new StringBuilder();
                                    for(int c = 0; c < maxCol; c++) {
                                        if ("Description".equals(keys.get(c))) {
                                            String description = rowData.get(keys.get(c));
                                            if (description.length() < 1000) {
                                                clobBasis.append("'").append(description).append("'");
                                            } else {
                                                int seg = description.length() / 500;
                                                for (int s = 0; s < seg; s++) {
                                                    clobBasis.append("TO_CLOB('").append(description, s * 500, (s + 1) * 500).append("')||");
                                                }
                                                clobBasis.append("TO_CLOB('").append(description.substring(seg * 500)).append("')");
                                            }
                                        } else {
                                            String subtitle = keys.get(c);
                                            if(columnList.contains(subtitle)) param.add(rowData.get(subtitle));
                                        }
                                    }
                                    inserted += exampleService.update(insert.toString().replace("#", clobBasis.toString()), param.toArray());
                                }
                                // Next Data Starts Handling, Remove Last Data Records
                                start = true;
                                for(String key : rowData.keySet()) {
                                    rowData.replace(key, "");
                                }
                                param.clear();
                                param.add(UUID.randomUUID().toString());
                            }
                            for(int c = 0; c < maxCol; c++) {
                                String curCell;
                                if (is_xls) {
                                    curCell = hst.getRow(r) == null ? "" : hst.getRow(r).getCell(c) == null ? "" : hst.getRow(r).getCell(c).toString();
                                } else {
                                    curCell = xst.getRow(r) == null ? "" : xst.getRow(r).getCell(c) == null ? "" : xst.getRow(r).getCell(c).toString();
                                }
                                // Store Information Among Multiple Line Concat With Comma
                                if(StringUtils.isNotBlank(rowData.get(keys.get(c))) && StringUtils.isNotBlank(curCell)) {
                                    rowData.replace(keys.get(c), rowData.get(keys.get(c)) + ", " + curCell);
                                } else {
                                    rowData.replace(keys.get(c), rowData.get(keys.get(c)) + curCell);
                                }
                            }
                        }
                        // Insert Last Data In Sheet Table
                        StringBuilder clobBasis = new StringBuilder();
                        for(int c = 0; c < maxCol; c++) {
                            if ("Description".equals(keys.get(c))) {
                                String description = rowData.get(keys.get(c));
                                if (description.length() < 1000) {
                                    clobBasis.append("'").append(description).append("'");
                                } else {
                                    int seg = description.length() / 500;
                                    for (int s = 0; s < seg; s++) {
                                        clobBasis.append("TO_CLOB('").append(description, s * 500, (s + 1) * 500).append("')||");
                                    }
                                    clobBasis.append("TO_CLOB('").append(description.substring(seg * 500)).append("')");
                                }
                            } else {
                                String subtitle = keys.get(c);
                                if(columnList.contains(subtitle)) param.add(rowData.get(subtitle));
                            }
                        }
                        inserted += exampleService.update(insert.toString().replace("#", clobBasis.toString()), param.toArray());
                    }
                }
                logger.info("Succeeded to Upload Excel Data");
                this.ajaxDoneSuccess(Integer.toString(inserted));
            }
        } catch (Exception e) {
            logger.error("Failed to Upload Excel Data: ", e);
            this.ajaxDoneFailure(null);
        } finally {
            boolean flag = false;
            if (file.isFile() && file.exists()) {
                flag = file.delete();
            }
            if (!flag) {
                logger.warn("Failed to Delete Handled Temporary File");
            }
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

    @GetMapping(value = "/download")
    public void download() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("assets/template/data_import_template.xlsx");
        if (inputStream != null) {
            logger.info("Load Template Resource Successfully");
            try {
                XSSFWorkbook wb = new XSSFWorkbook(inputStream);
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment;" + "filename=" + new String("data_import_example.xlsx".getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
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
