package basic.mvc.utility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
@Transactional
public class BaseDaoTest extends BaseDao<Object> {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public BaseDaoTest() {
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("spring-jdbc.xml");
            DataSource dataSource = (DataSource) context.getBean("oracleDataSource");
            jdbcTemplate.setDataSource(dataSource);
        } catch (Exception e) {
            throw new RuntimeException("Initialise Static Attributes Failed: ", e);
        }
    }

    @Before
    public void before() {
        super.jdbcTemplate = jdbcTemplate;
    }

    @Test
    public void find0() {
        List<Map<String, Object>> expect = jdbcTemplate.queryForList("SELECT COUNT(1) AS NUM FROM SYS_USER");
        List<Map<String, Object>> res = find("SELECT COUNT(1) AS NUM FROM SYS_USER");
        assertArrayEquals(expect.toArray(), res.toArray());
        assertEquals(expect.get(0), res.get(0));
        assertEquals(expect.get(0).get("NUM"), res.get(0).get("NUM"));
    }

}
