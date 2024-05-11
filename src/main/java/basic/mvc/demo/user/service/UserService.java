package basic.mvc.demo.user.service;

import basic.mvc.demo.user.po.User;
import basic.mvc.utility.PageList;
import basic.mvc.utility.Record;

import java.util.List;
import java.util.Map;

public interface UserService {

    User findObject(String sql, Object... param);

    PageList<Record> findPage(String sql, int page, int size, Object... param);

    int update(String sql, Object... param);

}
