package basic.mvc.utility;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseDao<T> {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    public SqlSessionTemplate sqlSessionTemplate;

    private final Logger logger = Logger.getLogger(this.getClass());

    public void execute(String sql) {
        jdbcTemplate.execute(sql);
    }

    public void call(CallableStatementCreator csc, List<SqlParameter> args) throws Exception {
        jdbcTemplate.call(csc, args);
    }

    public int update(String sql) throws Exception {
        return jdbcTemplate.update(sql);
    }

    public int update(String sql, Object args) throws Exception {
        return jdbcTemplate.update(sql, args);
    }

    public int update(String sql, Object[] args) throws Exception {
        return jdbcTemplate.update(sql, args);
    }

    public int[] batch(String sql, Object[][] args) throws Exception {
        ArrayList<Object[]> batchArgs = new ArrayList<>();
        if (args != null) {
            int cur = 0;
            for(int size = args.length; cur < size; ++cur) {
                batchArgs.add(args[cur]);
            }
        }
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    public int[] batch(String sql, List<Object[]> args) throws Exception {
        return jdbcTemplate.batchUpdate(sql, args);
    }

    public List<Map<String, Object>> find(String sql) throws Exception {
        if (StringUtils.isNotBlank(sql)) {
            sql = sql.toUpperCase();
        }
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> find(String sql, Object args) throws Exception {
        if (StringUtils.isNotBlank(sql)) {
            sql = sql.toUpperCase();
        }
        return jdbcTemplate.queryForList(sql, args);
    }

    public List<Map<String, Object>> find(String sql, Object[] args) throws Exception {
        if (StringUtils.isNotBlank(sql)) {
            sql = sql.toUpperCase();
        }
        return jdbcTemplate.queryForList(sql, args);
    }

    public Object find(Class<T> cls, String sql, Object[] args) throws Exception {
        return jdbcTemplate.queryForObject(sql, args, new BeanPropertyRowMapper<T>(cls));
    }

    public Object findObject(String sql, Class<T> cls) {
        return jdbcTemplate.queryForObject(sql, cls);
    }

    public Object findObject(String sql, Object[] args, Class<T> cls) {
        return jdbcTemplate.queryForObject(sql, args, cls);
    }

    public Map<String, Object> findMap(String sql) {
        try {
            if (StringUtils.isNotBlank(sql)) {
                sql = sql.toUpperCase();
            }
            return jdbcTemplate.queryForMap(sql);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public Map<String, Object> findMap(String sql, Object[] args) {
        try {
            if (StringUtils.isNotBlank(sql)) {
                sql = sql.toUpperCase();
            }
            return jdbcTemplate.queryForMap(sql, args);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

}
