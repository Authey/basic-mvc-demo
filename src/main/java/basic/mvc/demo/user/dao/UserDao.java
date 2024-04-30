package basic.mvc.demo.user.dao;

import basic.mvc.demo.user.po.User;
import basic.mvc.utility.BaseDao;
import org.springframework.stereotype.Repository;

@Repository("UserDao")
public class UserDao extends BaseDao<User> {

}
