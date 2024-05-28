package basic.mvc.demo.example.service;

import basic.mvc.utility.PageList;
import basic.mvc.utility.Record;

public interface ExampleService {

    PageList<Record> findPage(String sql, int page, int size, Object... param);

}
