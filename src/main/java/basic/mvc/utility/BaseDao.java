package basic.mvc.utility;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class BaseDao<T> {

    @Autowired
    public JdbcTemplate jdbcTemplate;

    public List<T> find(Class<T> cls, String sql, Object... args) {
        sql = StringUtils.isNotBlank(sql) ? sql.toUpperCase() : "";
        return jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<>(cls));
    }

    public List<Map<String, Object>> findList(String sql, Object... args) {
        sql = StringUtils.isNotBlank(sql) ? sql.toUpperCase() : "";
        return jdbcTemplate.queryForList(sql, args);
    }

    public T findObject(Class<T> cls, String sql, Object... args) {
        sql = StringUtils.isNotBlank(sql) ? sql.toUpperCase() : "";
        return jdbcTemplate.queryForObject(sql, args, new BeanPropertyRowMapper<>(cls));
    }

    public Map<String, Object> findMap(String sql, Object... args) {
        sql = StringUtils.isNotBlank(sql) ? sql.toUpperCase() : "";
        return jdbcTemplate.queryForMap(sql, args);
    }

    public int update(String sql, Object... args) {
        sql = StringUtils.isNotBlank(sql) ? sql.toUpperCase() : "";
        return jdbcTemplate.update(sql, args);
    }

    public int[] batch(String sql, Object[]... args) {
        sql = StringUtils.isNotBlank(sql) ? sql.toUpperCase() : "";
        ArrayList<Object[]> batchArgs = new ArrayList<>();
        if (args != null) {
            batchArgs.addAll(Arrays.asList(args));
        }
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }

}
