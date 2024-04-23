package basic.mvc.utility;

import java.io.Serializable;
import java.util.List;

public class PageList<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int page;

    private final int size;

    private final int pageTotal;

    private final long sizeTotal;

    private final List<T> innerList;

    public PageList(int page, int size, int pageTotal, long sizeTotal, List<T> list) {
        this.page = page;
        this.size = size;
        this.pageTotal = pageTotal;
        this.sizeTotal = sizeTotal;
        this.innerList = list;
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

    public List<T> getList() {
        return this.innerList;
    }

}
