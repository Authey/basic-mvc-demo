package basic.mvc.utility;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class PageListTest {

    List<Record> recList = new ArrayList<>();

    PageList<Record> pageRecList0;

    PageList<Record> pageRecList1;

    @Before
    public void before() {
        Record rec = new Record();
        rec.set("Key0", "Value0");
        rec.set("Key1", "Value1");
        recList.add(rec);
        recList.add(rec);
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

}
