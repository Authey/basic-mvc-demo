package basic.mvc.utility;

import basic.mvc.utility.exception.NoSuchElementFoundException;
import net.sf.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
    public void remove0() {

    }

    @Test
    public void remove1() {

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
