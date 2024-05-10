package basic.mvc.service.action;

import basic.mvc.utility.BaseController;
import basic.mvc.service.DataService;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/general_service")
public class GeneralServiceController extends BaseController {

    private final DataService dataService;

    private static final Map<String, Class<?>> primitiveTypeMap = new HashMap<>();

    static {
        primitiveTypeMap.put("byte", byte.class);
        primitiveTypeMap.put("short", short.class);
        primitiveTypeMap.put("int", int.class);
        primitiveTypeMap.put("long", long.class);
        primitiveTypeMap.put("float", float.class);
        primitiveTypeMap.put("double", double.class);
        primitiveTypeMap.put("char", char.class);
        primitiveTypeMap.put("boolean", boolean.class);
    }

    @Autowired
    public GeneralServiceController(DataService dataService) {
        this.dataService = dataService;
    }

    // 通用接口测试, 传入测试接口名称及参数类型即可通过Java反射机制调用接口
    @PostMapping(value = "/invoke")
    public void invoke(@RequestParam String interfaceName, @RequestParam String serviceClass, @RequestParam String paramTypes, HttpServletResponse response) {
        try {
            Object serviceInstance = serviceMap(Class.forName(serviceClass));
            List<Class<?>> baseTypes = new ArrayList<>();
            for (String type : paramTypes.split(",")) {
                baseTypes.add(primitiveTypeMap.containsKey(type) ? primitiveTypeMap.get(type) : Class.forName(type));
            }
            assert serviceInstance != null;
            Method serviceMethod = serviceInstance.getClass().getMethod(interfaceName, baseTypes.toArray(new Class<?>[0]));
            Parameter[] parameters = serviceMethod.getParameters();
            List<Object> params = new ArrayList<>();
            for (Parameter p : parameters) {
                String argName = p.getName();
                String curPara = this.getPara(argName);
                int index = Integer.parseInt(argName.substring(3));
                params.add(StringUtils.isNotBlank(curPara) ? classCast(baseTypes.get(index), curPara) : null);
            }
            this.renderJson(serviceMethod.invoke(serviceInstance, params.toArray()).toString());
        } catch (Exception e) {
            logger.error("调用" + interfaceName + "接口出错: ", e);
            JSONObject json = new JSONObject();
            json.put("state", 0);
            json.put("error", e.getMessage());
            this.renderJson(json.toString());
        }
    }

    private Object serviceMap(Class<?> serviceClass) {
        if (serviceClass.equals(DataService.class)) {
            return dataService;
        }
        return null;
    }

    private Object classCast(Class<?> clazz, String str) {
        if (clazz.equals(byte.class)) {
            return Byte.parseByte(str);
        } else if (clazz.equals(short.class)) {
            return Short.parseShort(str);
        } else if (clazz.equals(int.class)) {
            return Integer.parseInt(str);
        } else if (clazz.equals(long.class)) {
            return Long.parseLong(str);
        } else if (clazz.equals(float.class)) {
            return Float.parseFloat(str);
        } else if (clazz.equals(double.class)) {
            return Double.parseDouble(str);
        } else if (clazz.equals(boolean.class)) {
            return Boolean.parseBoolean(str);
        }
        return clazz.cast(str);
    }

}
