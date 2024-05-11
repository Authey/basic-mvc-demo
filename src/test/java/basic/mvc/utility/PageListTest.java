package basic.mvc.utility;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PageListTest {

    private final List<Record> recList = new ArrayList<>();

    private PageList<Record> pageRecList0;

    private PageList<Record> pageRecList1;

    @Before
    public void before() {
        Record rec0 = new Record();
        rec0.set("Key0", "Value0");
        rec0.set("Key1", "Value1");
        recList.add(rec0);
        Record rec1 = new Record();
        rec1.set("Key2", "Value2");
        rec1.set("Key3", "Value3");
        recList.add(rec1);
        pageRecList0 = new PageList<>(1, 5, 10, 50, recList);
        pageRecList1 = new PageList<>(2, 10, 5, 45, recList);
    }

    @Test
    public void getPage() {
        assertEquals(1, pageRecList0.getPage());
        assertEquals(2, pageRecList1.getPage());
    }

    @Test
    public void getSize() {
        assertEquals(5, pageRecList0.getSize());
        assertEquals(10, pageRecList1.getSize());
    }

    @Test
    public void getPageTotal() {
        assertEquals(10, pageRecList0.getPageTotal());
        assertEquals(5, pageRecList1.getPageTotal());
    }

    @Test
    public void getSizeTotal() {
        assertEquals(50, pageRecList0.getSizeTotal());
        assertEquals(45, pageRecList1.getSizeTotal());
    }

    @Test
    public void getList() {
        assertEquals(recList, pageRecList0.getList());
        assertEquals(recList, pageRecList1.getList());
        assertEquals(2, pageRecList0.getList().size());
        assertEquals(2, pageRecList1.getList().size());
        recList.clear();
        assertNotEquals(recList, pageRecList0.getList());
        assertNotEquals(recList, pageRecList1.getList());
        assertEquals(2, pageRecList0.getList().size());
        assertEquals(2, pageRecList1.getList().size());
        Record recNew = new Record();
        recNew.set("Key", "Value");
        recList.add(recNew);
        assertNotEquals(recList, pageRecList0.getList());
        assertNotEquals(recList, pageRecList1.getList());
        assertEquals(2, pageRecList0.getList().size());
        assertEquals(2, pageRecList1.getList().size());
        pageRecList0 = new PageList<>(1, 5, 10, 50, recList);
        pageRecList1 = new PageList<>(2, 10, 5, 45, recList);
        assertEquals(recList, pageRecList0.getList());
        assertEquals(recList, pageRecList1.getList());
        assertEquals(1, pageRecList0.getList().size());
        assertEquals(1, pageRecList1.getList().size());
    }

    @Test
    public void toJsonGrid() {
        JSONObject json0 = pageRecList0.toJsonGrid();
        JSONArray array0 = json0.getJSONArray("rows");
        assertTrue(json0.containsKey("rows"));
        JSONArray arrayTrue0 = new JSONArray();
        for (Record rec : pageRecList0.getList()) {
            JSONObject json = new JSONObject();
            json.put("cell", JSONObject.toJSON(rec.getData()));
            arrayTrue0.add(json);
        }
        assertArrayEquals(arrayTrue0.toArray(), array0.toArray());
        JSONObject json1 = pageRecList1.toJsonGrid();
        JSONArray array1 = json1.getJSONArray("rows");
        assertTrue(json1.containsKey("rows"));
        JSONArray arrayTrue1 = new JSONArray();
        for (Record rec : pageRecList1.getList()) {
            JSONObject json = new JSONObject();
            json.put("cell", JSONObject.toJSON(rec.getData()));
            arrayTrue1.add(json);
        }
        assertArrayEquals(arrayTrue1.toArray(), array1.toArray());
    }

    @Test
    public void toString0() {
        String str = pageRecList0.toString();
        JSONArray json0 = new JSONArray();
        json0.addAll(recList);
        assertEquals(json0.toJSONString(), str);
        assertTrue(JSONObject.parseObject(JSONArray.parseArray(str).get(0).toString()).containsKey("data"));
    }

}
