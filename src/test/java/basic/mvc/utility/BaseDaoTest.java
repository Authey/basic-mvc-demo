package basic.mvc.utility;

import basic.mvc.utility.exception.ParameterUnexpectedException;
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
import org.springframework.jdbc.UncategorizedSQLException;
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

    private final String id0 = "1EFC535F-34E0-4271-B43A-71E462539D29";
    
    private final String id1 = "0836F4F3-2974-48C7-8CDC-7AA3F53956A1";

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
    public void find0() {
        List<basic.mvc.demo.Test> expect = jdbcTemplate.query(select, new BeanPropertyRowMapper<>(clazz));
        List<basic.mvc.demo.Test> res = find(clazz, select);
        assertEquals(expect.size(), res.size());
        for(int i = 0; i < expect.size(); i++) {
            assertEquals(expect.get(i).toString(), res.get(i).toString());
            assertEquals(expect.get(i).getId(), res.get(i).getId());
        }
    }

    @Test
    public void find1() {
        List<basic.mvc.demo.Test> expect = jdbcTemplate.query(select + " WHERE ID = ?", new BeanPropertyRowMapper<>(clazz), id0);
        List<basic.mvc.demo.Test> res = find(clazz, select + " WHERE ID = ?", id0);
        assertEquals(expect.size(), res.size());
        for(int i = 0; i < expect.size(); i++) {
            assertEquals(expect.get(i).toString(), res.get(i).toString());
            assertEquals(expect.get(i).getId(), res.get(i).getId());
        }
    }

    @Test
    public void find2() {
        List<basic.mvc.demo.Test> expect = jdbcTemplate.query(select + " WHERE ID = ? AND FLAG = ?", new BeanPropertyRowMapper<>(clazz), id0, 1);
        List<basic.mvc.demo.Test> res = find(clazz, select + " WHERE ID = ? AND FLAG = ?", id0, 1);
        assertEquals(expect.size(), res.size());
        for(int i = 0; i < expect.size(); i++) {
            assertEquals(expect.get(i).toString(), res.get(i).toString());
            assertEquals(expect.get(i).getId(), res.get(i).getId());
        }
    }

    @Test
    public void find3() {
        List<basic.mvc.demo.Test> expect = jdbcTemplate.query(select + " WHERE ID = ?", new BeanPropertyRowMapper<>(clazz), "ID");
        List<basic.mvc.demo.Test> res = find(clazz, select + " WHERE ID = ?", "ID");
        assertEquals(expect.size(), res.size());
        for(int i = 0; i < expect.size(); i++) {
            assertEquals(expect.get(i).toString(), res.get(i).toString());
            assertEquals(expect.get(i).getId(), res.get(i).getId());
        }
    }

    @Test
    public void find4() {
        List<basic.mvc.demo.Test> expect = jdbcTemplate.query(select + " WHERE ID = ? AND FLAG = ?", new BeanPropertyRowMapper<>(clazz), id0, 2);
        List<basic.mvc.demo.Test> res = find(clazz, select + " WHERE ID = ? AND FLAG = ?", id0, 2);
        assertEquals(expect.size(), res.size());
        for(int i = 0; i < expect.size(); i++) {
            assertEquals(expect.get(i).toString(), res.get(i).toString());
            assertEquals(expect.get(i).getId(), res.get(i).getId());
        }
    }

    @Test(expected = BadSqlGrammarException.class)
    public void find5() {
        List<basic.mvc.demo.Test> res = find(clazz, select + " WHERE COLUMN = ?", "COL");
        assertEquals(0, res.size());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void find6() {
        List<basic.mvc.demo.Test> res = find(clazz, select + " WHERE CREATE_DATE = ?", "2024-05-09 16:03:27");
        assertEquals(0, res.size());
    }

    @Test
    public void findList0() {
        List<Map<String, Object>> expect = jdbcTemplate.queryForList(select);
        List<Map<String, Object>> res = findList(select);
        assertArrayEquals(expect.toArray(), res.toArray());
        assertEquals(expect.get(0), res.get(0));
        assertEquals(expect.get(0).get("ID"), res.get(0).get("ID"));
    }

    @Test
    public void findList1() {
        List<Map<String, Object>> expect = jdbcTemplate.queryForList(select + " WHERE ID = ?", id0);
        List<Map<String, Object>> res = findList(select + " WHERE ID = ?", id0);
        assertEquals(1, res.size());
        assertArrayEquals(expect.toArray(), res.toArray());
        assertEquals(expect.get(0), res.get(0));
        assertEquals(expect.get(0).get("ID"), res.get(0).get("ID"));
    }

    @Test
    public void findList2() {
        List<Map<String, Object>> expect = jdbcTemplate.queryForList(select + " WHERE ID = ? AND FLAG = ?", id0, 1);
        List<Map<String, Object>> res = findList(select + " WHERE ID = ? AND FLAG = ?", id0, 1);
        assertEquals(1, res.size());
        assertArrayEquals(expect.toArray(), res.toArray());
        assertEquals(expect.get(0), res.get(0));
        assertEquals(expect.get(0).get("ID"), res.get(0).get("ID"));
    }

    @Test
    public void findList3() {
        List<Map<String, Object>> expect = jdbcTemplate.queryForList(select + " WHERE ID = ?", "ID");
        List<Map<String, Object>> res = findList(select + " WHERE ID = ?", "ID");
        assertEquals(0, res.size());
        assertArrayEquals(expect.toArray(), res.toArray());
    }

    @Test
    public void findList4() {
        List<Map<String, Object>> expect = jdbcTemplate.queryForList(select + " WHERE ID = ? AND FLAG = ?", id0, 2);
        List<Map<String, Object>> res = findList(select + " WHERE ID = ? AND FLAG = ?", id0, 2);
        assertEquals(0, res.size());
        assertArrayEquals(expect.toArray(), res.toArray());
    }

    @Test(expected = BadSqlGrammarException.class)
    public void findList5() {
        List<Map<String, Object>> res = findList(select + " WHERE COLUMN = ?", "COL");
        assertEquals(0, res.size());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void findList6() {
        List<Map<String, Object>> res = findList(select + " WHERE CREATE_DATE = ?", "2024-05-09 16:03:27");
        assertEquals(0, res.size());
    }

    @Test
    public void findObject0() {
        basic.mvc.demo.Test expect = jdbcTemplate.queryForObject(select + " WHERE ID = ?", new BeanPropertyRowMapper<>(clazz), id0);
        basic.mvc.demo.Test res = findObject(clazz, select + " WHERE ID = ?", id0);
        assertEquals(expect.toString(), res.toString());
    }

    @Test(expected = BadSqlGrammarException.class)
    public void findObject1() {
        basic.mvc.demo.Test res = findObject(clazz, select + " WHERE COLUMN = ?", "COL");
        assertNull(res);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void findObject2() {
        basic.mvc.demo.Test res = findObject(clazz, select + " WHERE CREATE_DATE = ?", "2024-05-09 16:03:27");
        assertNull(res);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void findObject3() {
        basic.mvc.demo.Test res = findObject(clazz, select + " WHERE ID = ?", "ID");
        assertNull(res);
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void findObject4() {
        basic.mvc.demo.Test res = findObject(clazz, select);
        assertNull(res);
    }

    @Test
    public void findMap0() {
        Map<String, Object> expect = jdbcTemplate.queryForMap(select + " WHERE ID = ?", id0);
        Map<String, Object> res = findMap(select + " WHERE ID = ?", id0);
        assertEquals(expect.toString(), res.toString());
    }

    @Test(expected = BadSqlGrammarException.class)
    public void findMap1() {
        Map<String, Object> res = findMap(select + " WHERE COLUMN = ?", "COL");
        assertNull(res);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void findMap2() {
        Map<String, Object> res = findMap(select + " WHERE CREATE_DATE = ?", "2024-05-09 16:03:27");
        assertNull(res);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void findMap3() {
        Map<String, Object> res = findMap(select + " WHERE ID = ?", "ID");
        assertNull(res);
    }

    @Test(expected = IncorrectResultSizeDataAccessException.class)
    public void findMap4() {
        Map<String, Object> res = findMap(select);
        assertNull(res);
    }

    @Test
    public void findPage0() {
        PageList<Record> res = findPage(select, 1, 1);
        System.out.println(res.toJsonGrid());
        assertEquals(2, res.getPageTotal());
        assertEquals(2, res.getSizeTotal());
        assertEquals(1, res.getList().size());
    }

    @Test
    public void findPage1() {
        PageList<Record> res = findPage(select, 2, 1);
        System.out.println(res.getList());
        assertEquals(2, res.getPageTotal());
        assertEquals(2, res.getSizeTotal());
        assertEquals(1, res.getList().size());
    }

    @Test
    public void findPage2() {
        PageList<Record> res = findPage(select, 1, 2);
        assertEquals(1, res.getPageTotal());
        assertEquals(2, res.getSizeTotal());
        assertEquals(2, res.getList().size());
    }

    @Test
    public void findPage3() {
        PageList<Record> res = findPage(select, 2, 2);
        assertEquals(1, res.getPageTotal());
        assertEquals(2, res.getSizeTotal());
        assertEquals(0, res.getList().size());
    }

    @Test(expected = ParameterUnexpectedException.class)
    public void findPage4() {
        PageList<Record> res = findPage(select, 0, 1);
        assertEquals(1, res.getPageTotal());
        assertEquals(2, res.getSizeTotal());
        assertEquals(0, res.getList().size());
    }

    @Test(expected = ParameterUnexpectedException.class)
    public void findPage5() {
        PageList<Record> res = findPage(select, 1, 0);
        assertEquals(1, res.getPageTotal());
        assertEquals(2, res.getSizeTotal());
        assertEquals(0, res.getList().size());
    }

    @Test(expected = ParameterUnexpectedException.class)
    public void findPage6() {
        PageList<Record> res = findPage(select, 0, 0);
        assertEquals(1, res.getPageTotal());
        assertEquals(2, res.getSizeTotal());
        assertEquals(0, res.getList().size());
    }

    @Test
    public void findPage7() {
        PageList<Record> res = findPage(select + " WHERE ID = ?", 1, 2, id0);
        assertEquals(1, res.getPageTotal());
        assertEquals(1, res.getSizeTotal());
        assertEquals(1, res.getList().size());
    }

    @Test
    public void findPage8() {
        PageList<Record> res = findPage(select + " WHERE ID = ?", 1, 2, "ID");
        assertEquals(0, res.getPageTotal());
        assertEquals(0, res.getSizeTotal());
        assertEquals(0, res.getList().size());
    }

    @Test(expected = BadSqlGrammarException.class)
    public void findPage9() {
        PageList<Record> res = findPage(select + " WHERE COLUMN = ?", 1, 2, "COL");
        assertEquals(0, res.getPageTotal());
        assertEquals(0, res.getSizeTotal());
        assertEquals(0, res.getList().size());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void findPage10() {
        PageList<Record> res = findPage(select + " WHERE CREATE_DATE = ?", 1, 2, "2024-05-09 16:03:27");
        assertEquals(0, res.getPageTotal());
        assertEquals(0, res.getSizeTotal());
        assertEquals(0, res.getList().size());
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
        update(delete + " WHERE ID = ?", id0);
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

    @Test(expected = DataIntegrityViolationException.class)
    public void update4() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(delete + " WHERE CREATE_DATE = ?", "2024-05-09 16:03:27");
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
    }

    @Test
    public void update5() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(insert, UUID.randomUUID().toString(), Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "1", "Empty Description");
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev + 1, post);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void update6() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(insert, null, Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "1", "Empty Description");
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void update7() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(insert, UUID.randomUUID().toString(), "2024-05-09 16:03:27", "1", "Empty Description");
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
    }

    @Test(expected = DuplicateKeyException.class)
    public void update8() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        update(insert, id0, Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "1", "Empty Description");
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
    }

    @Test
    public void update9() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        update(update, "2");
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals("2", map.get("FLAG"));
        }
    }

    @Test
    public void update10() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        update(update + " WHERE ID = ?", 2, id0);
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals(id0.equals(map.get("ID")) ? "2" : "0", map.get("FLAG"));
        }
    }

    @Test
    public void update11() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        update(update + " WHERE ID = ?", 2, "ID");
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
    }

    @Test(expected = BadSqlGrammarException.class)
    public void update12() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        update(update + " WHERE COLUMN = ?", 2, "COL");
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
    }

    @Test(expected = UncategorizedSQLException.class)
    public void update13() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        update(update + ", ID = ? WHERE ID = ?", 2, null, id0);
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals(id0.equals(map.get("ID")) ? "2" : "0", map.get("FLAG"));
        }
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void update14() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        update(update + ", CREATE_DATE = ? WHERE ID = ?", 2, "2024-05-09 16:03:27", id0);
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals(id0.equals(map.get("ID")) ? "2" : "0", map.get("FLAG"));
        }
    }

    @Test(expected = DuplicateKeyException.class)
    public void update15() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        update(update + ", ID = ? WHERE ID = ?", 2, id1, id0);
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals(id0.equals(map.get("ID")) ? "2" : "0", map.get("FLAG"));
        }
    }

    @Test
    public void batch0() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        batch(delete + " WHERE ID = ?", new Object[]{id0}, new Object[]{id1});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev - 2, post);
    }

    @Test
    public void batch1() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        batch(delete + " WHERE ID = ?", new Object[]{id0}, new Object[]{"ID"});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev - 1, post);
    }

    @Test(expected = BadSqlGrammarException.class)
    public void batch2() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        batch(delete + " WHERE COLUMN = ?", new Object[]{"COL"});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void batch3() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        batch(delete + " WHERE CREATE_DATE = ?", new Object[]{Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()))}, new Object[]{"2024-05-09 16:03:27"});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev - 1, post);
    }

    @Test
    public void batch4() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        Object[] arg0 = new Object[]{UUID.randomUUID().toString(), Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "1", "Empty Description"};
        Object[] arg1 = new Object[]{UUID.randomUUID().toString(), Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "0", "Empty Description"};
        batch(insert, arg0, arg1);
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev + 2, post);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void batch5() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        Object[] arg0 = new Object[]{UUID.randomUUID().toString(), Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "1", "Empty Description"};
        Object[] arg1 = new Object[]{null, Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "0", "Empty Description"};
        batch(insert, arg0, arg1);
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev + 1, post);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void batch6() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        Object[] arg0 = new Object[]{UUID.randomUUID().toString(), Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "1", "Empty Description"};
        Object[] arg1 = new Object[]{UUID.randomUUID().toString(), "2024-05-09 16:03:27", "0", "Empty Description"};
        batch(insert, arg0, arg1);
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev + 1, post);
    }

    @Test(expected = DuplicateKeyException.class)
    public void batch7() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        Object[] arg0 = new Object[]{UUID.randomUUID().toString(), Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "1", "Empty Description"};
        Object[] arg1 = new Object[]{id0, Timestamp.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis())), "0", "Empty Description"};
        batch(insert, arg0, arg1);
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev + 1, post);
    }

    @Test
    public void batch8() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        batch(update + " WHERE ID = ?", new Object[]{0, id0}, new Object[]{1, id1});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals(id0.equals(map.get("ID")) ? "0" : "1", map.get("FLAG"));
        }
    }

    @Test
    public void batch9() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        batch(update + " WHERE ID = ?", new Object[]{0, id0}, new Object[]{1, "ID"});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals("0", map.get("FLAG"));
        }
    }

    @Test(expected = BadSqlGrammarException.class)
    public void batch10() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        batch(update + " WHERE COLUMN = ?", new Object[]{0, "COL"});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
    }

    @Test(expected = UncategorizedSQLException.class)
    public void batch11() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        batch(update + ", ID = ? WHERE ID = ?", new Object[]{0, null, id0});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals("0", map.get("FLAG"));
        }
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void batch12() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        batch(update + ", CREATE_DATE = ? WHERE ID = ?", new Object[]{0, "2024-05-09 16:03:27", id0});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals("0", map.get("FLAG"));
        }
    }

    @Test(expected = DuplicateKeyException.class)
    public void batch13() {
        int prev = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        List<Map<String, Object>> prevList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : prevList) {
            assertEquals(id0.equals(map.get("ID")) ? "1" : "0", map.get("FLAG"));
        }
        batch(update + ", ID = ? WHERE ID = ?", new Object[]{0, id1, id0});
        int post = JdbcTestUtils.countRowsInTable(jdbcTemplate, "TEST");
        assertEquals(prev, post);
        List<Map<String, Object>> postList = jdbcTemplate.queryForList(select);
        for(Map<String, Object> map : postList) {
            assertEquals("0", map.get("FLAG"));
        }
    }

}
