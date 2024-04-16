package basic.web.tests;

import basic.web.demo.BaseController;
import basic.web.service.DataService;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/general_test")
public class GeneralTestController extends BaseController {

    @Resource
    private DataService dataService;

    private static final Map<String, Class<?>[]> primitiveTypeMap = new HashMap<>();
    static {
        primitiveTypeMap.put("byte", new Class[]{byte.class, Byte.class});
        primitiveTypeMap.put("short", new Class[]{short.class, Short.class});
        primitiveTypeMap.put("int", new Class[]{int.class, Integer.class});
        primitiveTypeMap.put("long", new Class[]{long.class, Long.class});
        primitiveTypeMap.put("float", new Class[]{float.class, Float.class});
        primitiveTypeMap.put("double", new Class[]{double.class, Double.class});
        primitiveTypeMap.put("char", new Class[]{char.class, Character.class});
        primitiveTypeMap.put("boolean", new Class[]{boolean.class, Boolean.class});
    }

    // 通用接口测试, 传入测试接口名称及参数类型即可通过Java反射机制调用接口
    @RequestMapping(value = "/invoke", method = RequestMethod.POST)
    public void invoke(@RequestParam String interfaceName, @RequestParam String paramTypes, HttpServletResponse response) {
        try {
            List<Class<?>> baseTypes = new ArrayList<>();
            List<Class<?>> superTypes = new ArrayList<>();
            for (String type : paramTypes.split(",")) {
                if (primitiveTypeMap.containsKey(type)) {
                    Class<?>[] primeType = primitiveTypeMap.get(type);
                    baseTypes.add(primeType[0]);
                    superTypes.add(primeType[1]);
                } else {
                    Class<?> advanceType = Class.forName(type);
                    baseTypes.add(advanceType);
                    superTypes.add(advanceType);
                }
            }
            Method serviceInterface = dataService.getClass().getMethod(interfaceName, baseTypes.toArray(new Class<?>[0]));
            Parameter[] parameters = serviceInterface.getParameters();
            List<Object> params = new ArrayList<>();
            for (Parameter p : parameters) {
                String argName = p.getName();
                String curPara = request.getParameter(argName);
                int index = Integer.parseInt(argName.substring(3));
                params.add(StringUtils.isNotBlank(curPara) ? classCast(superTypes.get(index), curPara) : null);
            }
            this.renderJson(response, serviceInterface.invoke(dataService, params.toArray()).toString());
        } catch (Exception e) {
            logger.error("调用" + interfaceName + "接口出错: ", e);
            JSONObject json = new JSONObject();
            json.put("state", 0);
            json.put("error", e.getMessage());
            this.renderJson(response, json.toString());
        }
    }

}
