package basic.mvc.demo.user.po;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @JSONField(name = "ID")
    private String id;

    @JSONField(name = "USERNAME")
    private String username;

    @JSONField(name = "PASSWORD")
    private String password;

    @JSONField(name = "AUTH_LEVEL")
    private String authLevel;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setAuthLevel(String authLevel) {
        this.authLevel = authLevel;
    }

    public String getAuthLevel() {
        return authLevel;
    }

}
