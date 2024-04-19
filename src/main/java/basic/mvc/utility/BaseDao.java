package basic.mvc.utility;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;

public abstract class BaseDao<T> {

    @Resource
    protected JdbcTemplate jdbcTemplate;

}
