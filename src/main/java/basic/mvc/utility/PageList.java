package basic.mvc.utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageList<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int page;

    private final int size;

    private final int pageTotal;

    private final long sizeTotal;

    private final List<E> innerList;

    public PageList(int page, int size, int pageTotal, long sizeTotal, List<E> list) {
        this.page = page;
        this.size = size;
        this.pageTotal = pageTotal;
        this.sizeTotal = sizeTotal;
        this.innerList = new ArrayList<>(list);
    }

    public int getPage() {
        return this.page;
    }

    public int getSize() {
        return this.size;
    }

    public int getPageTotal() {
        return this.pageTotal;
    }

    public long getSizeTotal() {
        return this.sizeTotal;
    }

    public List<E> getList() {
        return this.innerList;
    }

}
