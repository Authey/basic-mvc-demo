package basic.mvc.utility;

import basic.mvc.utility.exception.ParameterUnexpectedException;
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

    public PageList<Record> findPage(String sql, int page, int size, Object... args) {
        if (page >= 1 && size >= 1) {
            String count = "SELECT COUNT(1) " + sql.substring(sql.indexOf("FROM"));
            Number num = jdbcTemplate.queryForObject(count, args, Long.class);
            if (num != null) {
                long sizeTotal = num.longValue();
                String paginate = "SELECT * FROM (SELECT ROW_.*, ROWNUM ROWNUM_ FROM (" + sql + ") ROW_ WHERE ROWNUM <= " + page * size + ") ALIAS WHERE ALIAS.ROWNUM_ >= " + ((page - 1) * size + 1);
                List<Map<String, Object>> mapList = jdbcTemplate.queryForList(paginate, args);
                List<Record> recList = new ArrayList<>();
                for (Map<String, Object> map : mapList) {
                    Record rec = new Record();
                    rec.set(map);
                    recList.add(rec);
                }
                return new PageList<>(page, size, (int) Math.ceil((double) sizeTotal / size), sizeTotal, recList);
            } else {
                return new PageList<>(page, size, 0, 0L, new ArrayList<>());
            }
        } else {
            throw new ParameterUnexpectedException("Parameter Page [" + page + "] Or Size [" + size + "] Is Unexpected");
        }
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
