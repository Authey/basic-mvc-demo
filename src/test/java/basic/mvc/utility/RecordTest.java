package basic.mvc.utility;

import basic.mvc.utility.exception.MapPairUnbalancedException;
import basic.mvc.utility.exception.NoSuchElementFoundException;
import net.sf.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class RecordTest {

    Record record = new Record();

    Map<String, Object> map = new HashMap<>();

    @Before
    public void before() throws NoSuchFieldException, IllegalAccessException {
        Class<? extends Record> recClass = record.getClass();
        Field data = recClass.getDeclaredField("data");
        data.setAccessible(true);
        map.put("Key0", "Value0");
        map.put("Key1", "Value1");
        Map<String, Object> newMap = new HashMap<>(map);
        data.set(record, newMap);
    }

    @Test
    public void get0() {
        String res0 = (String) record.get("Key0");
        assertEquals("Value0", res0);
        String res1 = (String) record.get("Key1");
        assertEquals("Value1", res1);
    }

    @Test(expected = NoSuchElementFoundException.class)
    public void get1() {
        String res = (String) record.get("Key2");
        assertNotEquals("Value", res);
    }

    @Test
    public void get2() {
        String res0 = (String) record.get("Key0", "Value2");
        assertEquals("Value0", res0);
        String res1 = (String) record.get("Key1", "Value2");
        assertEquals("Value1", res1);
    }

    @Test
    public void get3() {
        String res = (String) record.get("Key2", "Value");
        assertEquals("Value", res);
    }

    @Test
    public void set0() {
        assertEquals(map, record.getData());
        assertFalse(map.containsKey("Key2"));
        record.set("Key2", "Value2");
        assertNotEquals(map, record.getData());
        assertFalse(map.containsKey("Key2"));
        assertTrue(record.getData().containsKey("Key2"));
        assertEquals("Value2", record.get("Key2"));
    }

    @Test
    public void set1() {
        assertEquals(map, record.getData());
        assertTrue(map.containsKey("Key1"));
        assertEquals("Value1", record.get("Key1"));
        record.set("Key1", "Value2");
        assertNotEquals(map, record.getData());
        assertTrue(record.getData().containsKey("Key1"));
        assertEquals("Value2", record.get("Key1"));
    }

    @Test
    public void set2() {
        assertEquals(map, record.getData());
        assertFalse(map.containsKey("Key2"));
        assertFalse(map.containsKey("Key3"));
        record.set(new String[]{"Key2", "Key3"}, "Value2", "Value3");
        assertNotEquals(map, record.getData());
        assertFalse(map.containsKey("Key2"));
        assertFalse(map.containsKey("Key3"));
        assertTrue(record.getData().containsKey("Key2"));
        assertTrue(record.getData().containsKey("Key3"));
        assertEquals("Value2", record.get("Key2"));
        assertEquals("Value3", record.get("Key3"));
    }

    @Test
    public void set3() {
        assertEquals(map, record.getData());
        assertTrue(map.containsKey("Key0"));
        assertTrue(map.containsKey("Key1"));
        assertEquals("Value0", record.get("Key0"));
        assertEquals("Value1", record.get("Key1"));
        record.set(new String[]{"Key0", "Key1"}, "Value2", "Value3");
        assertNotEquals(map, record.getData());
        assertTrue(record.getData().containsKey("Key0"));
        assertTrue(record.getData().containsKey("Key1"));
        assertEquals("Value2", record.get("Key0"));
        assertEquals("Value3", record.get("Key1"));
    }

    @Test(expected = MapPairUnbalancedException.class)
    public void set4() {
        assertEquals(map, record.getData());
        assertFalse(map.containsKey("Key2"));
        assertFalse(map.containsKey("Key3"));
        record.set(new String[]{"Key2", "Key3"}, "Value2");
    }

    @Test
    public void set5() {
        assertEquals(map, record.getData());
        assertFalse(map.containsKey("Key2"));
        Map<String, Object> subMap = new HashMap<>();
        subMap.put("Key2", "Value2");
        record.set(subMap);
        assertNotEquals(map, record.getData());
        assertFalse(map.containsKey("Key2"));
        assertTrue(record.getData().containsKey("Key2"));
        assertEquals("Value2", record.get("Key2"));
    }

    @Test
    public void set6() {
        assertEquals(map, record.getData());
        assertTrue(map.containsKey("Key1"));
        assertEquals("Value1", record.get("Key1"));
        Map<String, Object> subMap = new HashMap<>();
        subMap.put("Key1", "Value2");
        record.set(subMap);
        assertNotEquals(map, record.getData());
        assertTrue(record.getData().containsKey("Key1"));
        assertEquals("Value2", record.get("Key1"));
    }

    @Test
    public void set7() {
        assertEquals(map, record.getData());
        assertFalse(map.containsKey("Key2"));
        Record subRec = new Record();
        subRec.set("Key2", "Value2");
        record.set(subRec);
        assertNotEquals(map, record.getData());
        assertFalse(map.containsKey("Key2"));
        assertTrue(record.getData().containsKey("Key2"));
        assertEquals("Value2", record.get("Key2"));
    }

    @Test
    public void set8() {
        assertEquals(map, record.getData());
        assertTrue(map.containsKey("Key1"));
        assertEquals("Value1", record.get("Key1"));
        Record subRec = new Record();
        subRec.set("Key1", "Value2");
        record.set(subRec);
        assertNotEquals(map, record.getData());
        assertTrue(record.getData().containsKey("Key1"));
        assertEquals("Value2", record.get("Key1"));
    }

    @Test
    public void remove0() {
        assertEquals(map, record.getData());
        assertTrue(map.containsKey("Key0"));
        record.remove("Key0");
        assertNotEquals(map, record.getData());
        assertTrue(map.containsKey("Key0"));
        assertFalse(record.getData().containsKey("Key0"));
    }

    @Test
    public void remove1() {
        assertEquals(map, record.getData());
        assertTrue(map.containsKey("Key0"));
        assertTrue(map.containsKey("Key1"));
        record.remove("Key0", "Key1");
        assertNotEquals(map, record.getData());
        assertTrue(map.containsKey("Key0"));
        assertTrue(map.containsKey("Key1"));
        assertFalse(record.getData().containsKey("Key0"));
        assertFalse(record.getData().containsKey("Key1"));
    }

    @Test
    public void getDataKeys() {
        List<String> keys = record.getDataKeys();
        List<String> mapKeys = new ArrayList<>(map.keySet());
        assertEquals(mapKeys, keys);
        map.put("Keys", "Values");
        List<Object> newKeys = new ArrayList<>(map.values());
        assertNotEquals(newKeys, keys);
    }

    @Test
    public void getDataValues() {
        List<Object> values = record.getDataValues();
        List<Object> mapValues = new ArrayList<>(map.values());
        assertEquals(mapValues, values);
        map.put("Keys", "Values");
        List<Object> newValues = new ArrayList<>(map.values());
        assertNotEquals(newValues, values);
    }

    @Test
    public void getData() {
        Map<String, Object> data = record.getData();
        assertEquals(map, data);
        map.put("Keys", "Values");
        assertNotEquals(map, data);
    }

    @Test
    public void clear() {
        record.clear();
        assertEquals(new HashMap<>(), record.getData());
        assertNotEquals(new HashMap<>(), map);
    }

    @Test
    public void toJson() {
        JSONObject json = record.toJson();
        assertEquals(JSONObject.fromObject(map), json);
        map.put("Keys", "Values");
        JSONObject newJson = record.toJson();
        assertNotEquals(JSONObject.fromObject(map), newJson);
    }

    @Test
    public void hashCode0() {
        int hash = record.hashCode();
        assertEquals(map.hashCode(), hash);
        map.put("Keys", "Values");
        int newHash = record.hashCode();
        assertNotEquals(map.hashCode(), newHash);
    }

    @Test
    public void toString0() {
        String str = record.toString();
        assertEquals(JSONObject.fromObject(map).toString(), str);
        map.put("Keys", "Values");
        String newStr = record.toString();
        assertNotEquals(JSONObject.fromObject(map).toString(), newStr);
    }

    @After
    public void after() {
        record = new Record();
        map = new HashMap<>();
    }

}
