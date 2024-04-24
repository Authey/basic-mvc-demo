package basic.mvc.utility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PageListTest {

    List<Record> recList = new ArrayList<>();

    PageList<Record> pageRecList0 = new PageList<>(1, 5, 10, 50, recList);

    PageList<Record> pageRecList1 = new PageList<>(2, 10, 5, 45, recList);

    @Before
    public void before() {
        Record rec = new Record();
        rec.set("Key0", "Value0");
        rec.set("Key1", "Value1");
        recList.add(rec);
        recList.add(rec);
    }

    @Test
    public void getPageTest() {
        assertEquals(1, pageRecList0.getPage());
        assertEquals(2, pageRecList1.getPage());
    }

    @Test
    public void getSizeTest() {
        assertEquals(5, pageRecList0.getSize());
        assertEquals(10, pageRecList1.getSize());
    }

    @Test
    public void getPageTotalTest() {
        assertEquals(10, pageRecList0.getPageTotal());
        assertEquals(5, pageRecList1.getPageTotal());
    }

    @Test
    public void getSizeTotalTest() {
        assertEquals(50, pageRecList0.getSizeTotal());
        assertEquals(45, pageRecList1.getSizeTotal());
    }

    @Test
    public void getListTest() {
        assertEquals(recList, pageRecList0.getList());
        assertEquals(recList, pageRecList1.getList());
        assertEquals(2, pageRecList0.getList().size());
        assertEquals(2, pageRecList1.getList().size());
        recList.clear();
        assertEquals(recList, pageRecList0.getList());
        assertEquals(recList, pageRecList1.getList());
        assertEquals(0, pageRecList0.getList().size());
        assertEquals(0, pageRecList1.getList().size());
        Record recNew = new Record();
        recNew.set("Key", "Value");
        recList.add(recNew);
        assertEquals(recList, pageRecList0.getList());
        assertEquals(recList, pageRecList1.getList());
        assertEquals(1, pageRecList0.getList().size());
        assertEquals(1, pageRecList1.getList().size());
    }

}
