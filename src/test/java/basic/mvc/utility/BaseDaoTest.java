package basic.mvc.utility;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
@Transactional
public class BaseDaoTest extends BaseDao<Object> {

    private static final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    private static final SqlSessionTemplate sqlSessionTemplate;

    static {
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("spring-jdbc.xml");
            DataSource dataSource = (DataSource) context.getBean("oracleDataSource");
            Resource configuration = new ClassPathResource("mybatis-config.xml");
            PathMatchingResourcePatternResolver resource = new PathMatchingResourcePatternResolver();
            Resource[] mappers = resource.getResources("mappers/*.xml");
            jdbcTemplate.setDataSource(dataSource);
            SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
            sqlSessionFactory.setDataSource(dataSource);
            sqlSessionFactory.setConfigLocation(configuration);
            sqlSessionFactory.setMapperLocations(mappers);
            sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory.getObject());
        } catch (Exception e) {
            throw new RuntimeException("Initialise Static Attributes Failed: ", e);
        }
    }

    @Before
    public void before() {
        super.jdbcTemplate = jdbcTemplate;
        super.sqlSessionTemplate = sqlSessionTemplate;
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
