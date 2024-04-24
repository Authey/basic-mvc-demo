package basic.mvc.utility;

import basic.mvc.utility.exception.MapPairUnbalancedException;
import basic.mvc.utility.exception.NoSuchElementFoundException;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.*;

public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, Object> data = new HashMap<>();

    public Object get(String key) {
        if (this.data.containsKey(key)) {
            return this.data.get(key);
        }
        throw new NoSuchElementFoundException("No Such Key [" + key + "] Found in This Record");
    }

    public Object get(String key, Object defaultValue) {
        return this.data.getOrDefault(key, defaultValue);
    }

    public void set(String key, Object value) {
        this.data.put(key, value);
    }

    public void set(String[] keys, Object... values) {
        if (keys.length != values.length) {
            throw new MapPairUnbalancedException("Key Value Pair Has [" + keys.length + ", " + values.length + "] Elements Which is Unbalanced");
        }
        for (int i = 0; i < keys.length; i++) {
            this.data.put(keys[i], values[i]);
        }
    }

    public void set(Map<? extends String, ?> map) {
        this.data.putAll(map);
    }

    public void set(Record record) {
        this.data.putAll(record.getData());
    }

    public void remove(String key) {
        this.data.remove(key);
    }

    public void remove(String... keys) {
        for(String k : keys) {
            this.data.remove(k);
        }
    }

    public List<String> getDataKeys() {
        return new ArrayList<>(this.data.keySet());
    }

    public List<Object> getDataValues() {
        return new ArrayList<>(this.data.values());
    }

    public Map<String, Object> getData() {
        return this.data;
    }

    public void clear() {
        this.data.clear();
    }

    public JSONObject toJson () {
        return JSONObject.fromObject(this.data);
    }

    @Override
    public int hashCode() {
        return this.data.hashCode();
    }

    @Override
    public String toString () {
        return JSONObject.fromObject(this.data).toString();
    }

}
