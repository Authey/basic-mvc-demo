package basic.mvc.utility;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class BaseDaoTest extends BaseDao<basic.mvc.demo.Test> {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate();

    private final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();

    private TransactionStatus status;

    private final Class<basic.mvc.demo.Test> clazz = basic.mvc.demo.Test.class;

    private final String id = "1EFC535F-34E0-4271-B43A-71E462539D29";

    private final String select = "SELECT * FROM TEST";

    private final String insert = "INSERT INTO TEST (ID, CREATE_DATE, FLAG, DESCRIPTION) VALUES (?, ?, ?, ?)";

    private final String update = "UPDATE TEST SET FLAG = ?";

    private final String delete = "DELETE FROM TEST";

    public BaseDaoTest() {
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("spring-jdbc.xml");
            DataSource dataSource = (DataSource) context.getBean("oracleDataSource");
            jdbcTemplate.setDataSource(dataSource);
            transactionManager.setDataSource(dataSource);
        } catch (Exception e) {
            throw new RuntimeException("Initialise Static Attributes Failed: ", e);
        }
    }

    @Before
    public void before() {
        super.jdbcTemplate = jdbcTemplate;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        status = transactionManager.getTransaction(def);
    }

    @After
    public void after() {
        transactionManager.rollback(status);
    }

    @Test
    public void update0() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(delete);
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev - 2, post);
    }

    @Test
    public void update1() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(delete + " WHERE ID = ?", id);
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev - 1, post);
    }

    @Test
    public void update2() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(delete + " WHERE ID = ?", "ID");
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
    }

    @Test(expected = BadSqlGrammarException.class)
    public void update3() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(delete + " WHERE COLUMN = ?", "COL");
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
    }

    @Test
    public void update4() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(insert, new Object[]{UUID.randomUUID().toString(), Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "1", "Empty Description"});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev + 1, post);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void update5() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(insert, new Object[]{UUID.randomUUID().toString(), "2024-05-09 16:03:27", "1", "Empty Description"});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
    }

    @Test(expected = DuplicateKeyException.class)
    public void update6() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(insert, new Object[]{id, Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "1", "Empty Description"});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
    }

    @Test
    public void update7() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(update, "2");
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : list) {
            assertEquals("2", map.get("FLAG"));
        }
    }

    @Test
    public void update8() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(update + " WHERE ID = ?", new Object[]{2, id});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : list) {
            assertEquals(id.equals(map.get("ID")) ? "2" : "0", map.get("FLAG"));
        }
    }

    @Test
    public void update9() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(update + " WHERE ID = ?", new Object[]{2, "ID"});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : list) {
            assertEquals(id.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
    }

    @Test(expected = BadSqlGrammarException.class)
    public void update10() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(update + " WHERE COLUMN = ?", new Object[]{2, "COL"});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : list) {
            assertEquals("2", map.get("FLAG"));
        }
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void update11() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(update + ", CREATE_DATE = ? WHERE ID = ?", new Object[]{2, "2024-05-09 16:03:27", id});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : list) {
            assertEquals(id.equals(map.get("ID")) ? "2" : "0", map.get("FLAG"));
        }
    }

    @Test(expected = DuplicateKeyException.class)
    public void update12() {
        String uid = "0836F4F3-2974-48C7-8CDC-7AA3F53956A1";
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(update + ", ID = ? WHERE ID = ?", new Object[]{2, uid, id});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : list) {
            assertEquals(id.equals(map.get("ID")) ? "2" : "0", map.get("FLAG"));
        }
    }

    @Test
    public void batch() {

    }

    @Test
    public void find0() {
        List<Map<String, Object>> expect = jdbcTemplate.queryForList(select);
        List<Map<String, Object>> res = find(select);
        assertArrayEquals(expect.toArray(), res.toArray());
        assertEquals(expect.get(0), res.get(0));
        assertEquals(expect.get(0).get("NUM"), res.get(0).get("NUM"));
    }

    @Test
    public void find1() {
        List<Map<String, Object>> expect = jdbcTemplate.queryForList(select + " WHERE ID = ?", id);
        List<Map<String, Object>> res = find(select + " WHERE ID = ?", id);
        assertEquals(1, res.size());
        assertArrayEquals(expect.toArray(), res.toArray());
        assertEquals(expect.get(0), res.get(0));
        assertEquals(expect.get(0).get("NUM"), res.get(0).get("NUM"));
    }

    @Test
    public void find2() {
        List<Map<String, Object>> expect = jdbcTemplate.queryForList(select + " WHERE ID = ? AND FLAG = ?", id, 1);
        List<Map<String, Object>> res = find(select + " WHERE ID = ? AND FLAG = ?", new Object[]{id, 1});
        assertEquals(1, res.size());
        assertArrayEquals(expect.toArray(), res.toArray());
        assertEquals(expect.get(0), res.get(0));
        assertEquals(expect.get(0).get("NUM"), res.get(0).get("NUM"));
    }

    @Test
    public void find3() {
        List<Map<String, Object>> expect = jdbcTemplate.queryForList(select + " WHERE ID = ?", "ID");
        List<Map<String, Object>> res = find(select + " WHERE ID = ?", "ID");
        assertEquals(0, res.size());
        assertArrayEquals(expect.toArray(), res.toArray());
    }

    @Test
    public void find4() {
        List<Map<String, Object>> expect = jdbcTemplate.queryForList(select + " WHERE ID = ? AND FLAG = ?", id, 2);
        List<Map<String, Object>> res = find(select + " WHERE ID = ? AND FLAG = ?", new Object[]{id, 2});
        assertEquals(0, res.size());
        assertArrayEquals(expect.toArray(), res.toArray());
    }

    @Test(expected = BadSqlGrammarException.class)
    public void find5() {
        List<Map<String, Object>> res = find(select + " WHERE COLUMN = ?", "COL");
        assertEquals(0, res.size());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void find6() {
        List<Map<String, Object>> res = find(select + " WHERE CREATE_DATE = ?", "2024-05-09 16:03:27");
        assertEquals(0, res.size());
    }

    @Test
    public void find7() {
        basic.mvc.demo.Test expect = jdbcTemplate.queryForObject(select + " WHERE ID = ?", new Object[]{id}, new BeanPropertyRowMapper<>(clazz));
        basic.mvc.demo.Test res = find(clazz, select + " WHERE ID = ?", new Object[]{id});
        assertEquals(expect.toString(), res.toString());
    }

    @Test(expected = BadSqlGrammarException.class)
    public void find8() {
        basic.mvc.demo.Test res = find(clazz, select + " WHERE COLUMN = ?", new Object[]{"COL"});
        assertNull(res);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void find9() {
        basic.mvc.demo.Test res = find(clazz, select + " WHERE CREATE_DATE = ?", new Object[]{"2024-05-09 16:03:27"});
        assertNull(res);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void find10() {
        basic.mvc.demo.Test res = find(clazz, select + " WHERE ID = ?", new Object[]{"ID"});
        assertNull(res);
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void find11() {
        basic.mvc.demo.Test res = find(clazz, select, new Object[]{});
        assertNull(res);
    }

}
