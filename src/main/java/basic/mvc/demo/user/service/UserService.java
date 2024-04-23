package basic.mvc.demo.user.service;

import java.util.List;
import java.util.Map;

public interface UserService {

    public List<Map<String, Object>> find(String sql) throws Exception;

    public List<Map<String, Object>> find(String sql, Object param) throws Exception;

    public List<Map<String, Object>> find(String sql, Object[] param) throws Exception;

    public int update(String sql) throws Exception;

    public int update(String sql, Object[] param) throws Exception;

}
