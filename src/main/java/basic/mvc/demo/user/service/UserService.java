package basic.mvc.demo.user.service;

import basic.mvc.demo.user.po.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<Map<String, Object>> findList(String sql, Object... param);

    User findObject(String sql, Object... param);

    int update(String sql, Object... param);

}
