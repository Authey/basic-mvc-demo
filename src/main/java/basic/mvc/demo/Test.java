package basic.mvc.demo;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class Test implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "ID")
    private String id;

    @JSONField(name = "CREATE_DATE")
    private String createDate;

    @JSONField(name = "FLAG")
    private String flag;

    @JSONField(name = "DESCRIPTION")
    private String description;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return String.join(" : ", getId(), getCreateDate(), getFlag(), getDescription());
    }

}
