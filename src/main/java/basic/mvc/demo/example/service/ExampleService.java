package basic.mvc.demo.example.service;

import basic.mvc.utility.PageList;
import basic.mvc.utility.Record;

import java.util.List;
import java.util.Map;

public interface ExampleService {

    List<Map<String, Object>> findList(String sql, Object... args);

    PageList<Record> findPage(String sql, int page, int size, Object... param);

    int update(String sql, Object... param);

}
