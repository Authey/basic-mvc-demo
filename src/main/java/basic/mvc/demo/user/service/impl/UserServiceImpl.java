package basic.mvc.demo.user.service.impl;

import basic.mvc.demo.user.dao.UserDao;
import basic.mvc.demo.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("UserServiceImpl")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    public List<Map<String, Object>> find(String sql, Object... param) {
        return userDao.findList(sql, param);
    }

    public int update(String sql, Object... param) {
        return userDao.update(sql, param);
    }

}
