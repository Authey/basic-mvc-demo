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

    public void execute(String var1) {
        this.jdbcTemplate.execute(var1);
    }

    public void call(CallableStatementCreator var1, List<SqlParameter> var2) throws Exception {
        this.jdbcTemplate.call(var1, var2);
    }

    public int update(String var1) throws Exception {
        return this.jdbcTemplate.update(var1);
    }

    public int update(String var1, Object var2) throws Exception {
        return this.jdbcTemplate.update(var1, var2);
    }

    public int update(String var1, Object[] var2) throws Exception {
        return this.jdbcTemplate.update(var1, var2);
    }

    public int[] batch(String var1, Object[][] var2) throws Exception {
        ArrayList<Object[]> var3 = new ArrayList<>();
        if (var2 != null) {
            int var4 = 0;
            for(int var5 = var2.length; var4 < var5; ++var4) {
                var3.add(var2[var4]);
            }
        }
        return this.jdbcTemplate.batchUpdate(var1, var3);
    }

    public int[] batch(String var1, List<Object[]> var2) throws Exception {
        return this.jdbcTemplate.batchUpdate(var1, var2);
    }

    public List<Map<String, Object>> find(String var1) throws Exception {
        if (StringUtils.isNotBlank(var1)) {
            var1 = var1.toUpperCase();
        }
        return this.jdbcTemplate.queryForList(var1);
    }

    public List<Map<String, Object>> find(String var1, Object var2) throws Exception {
        if (StringUtils.isNotBlank(var1)) {
            var1 = var1.toUpperCase();
        }
        return this.jdbcTemplate.queryForList(var1, var2);
    }

    public List<Map<String, Object>> find(String var1, Object[] var2) throws Exception {
        if (StringUtils.isNotBlank(var1)) {
            var1 = var1.toUpperCase();
        }
        return this.jdbcTemplate.queryForList(var1, var2);
    }

    public Object find(Class<T> var1, String var2, Object[] var3) throws Exception {
        return this.jdbcTemplate.queryForObject(var2, var3, new BeanPropertyRowMapper<T>(var1));
    }

    public Object findObject(String var1, Class<T> var2) {
        return this.jdbcTemplate.queryForObject(var1, var2);
    }

    public Object findObject(String var1, Object[] var2, Class<T> var3) {
        return this.jdbcTemplate.queryForObject(var1, var2, var3);
    }

    public Map<String, Object> findMap(String var1) {
        try {
            if (StringUtils.isNotBlank(var1)) {
                var1 = var1.toUpperCase();
            }
            return this.jdbcTemplate.queryForMap(var1);
        } catch (Exception var3) {
            logger.error(var3.getMessage());
            return null;
        }
    }

    public Map<String, Object> findMap(String var1, Object[] var2) {
        try {
            if (StringUtils.isNotBlank(var1)) {
                var1 = var1.toUpperCase();
            }
            return this.jdbcTemplate.queryForMap(var1, var2);
        } catch (Exception var4) {
            logger.error(var4.getMessage());
            return null;
        }
    }

}
