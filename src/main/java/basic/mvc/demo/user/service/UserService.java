package basic.mvc.demo.user.service;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<Map<String, Object>> find(String sql, Object... param) throws Exception;

    int update(String sql, Object... param) throws Exception;

}
