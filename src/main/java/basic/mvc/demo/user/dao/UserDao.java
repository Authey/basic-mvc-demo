package basic.mvc.demo.user.dao;

import basic.mvc.demo.user.po.User;
import basic.mvc.utility.BaseDao;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ListResourceBundle;
import java.util.Map;

@Repository("UserDao")
public class UserDao extends BaseDao<User> {

}
