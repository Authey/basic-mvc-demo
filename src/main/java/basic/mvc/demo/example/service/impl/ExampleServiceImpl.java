package basic.mvc.demo.example.service.impl;

import basic.mvc.demo.example.dao.ExampleDao;
import basic.mvc.demo.example.service.ExampleService;
import basic.mvc.utility.PageList;
import basic.mvc.utility.Record;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("ExampleServiceImpl")
@Transactional
public class ExampleServiceImpl implements ExampleService {

    @Resource
    private ExampleDao exampleDao;

    public List<Map<String, Object>> findList(String sql, Object... args) {
        return exampleDao.findList(sql, args);
    }

    public PageList<Record> findPage(String sql, int page, int size, Object... param) {
        return exampleDao.findPage(sql, page, size, param);
    }

    public int update(String sql, Object... param) {
        return exampleDao.update(sql, param);
    }

}
