package basic.mvc.demo.user.service.impl;

import basic.mvc.demo.user.dao.UserDao;
import basic.mvc.demo.user.po.User;
import basic.mvc.demo.user.service.UserService;
import basic.mvc.utility.PageList;
import basic.mvc.utility.Record;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service("UserServiceImpl")
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    public User findObject(String sql, Object... param) {
        return userDao.findObject(User.class, sql, param);
    }

    public PageList<Record> findPage(String sql, int page, int size, Object... param) {
        return userDao.findPage(sql, page, size, param);
    }

    public int update(String sql, Object... param) {
        return userDao.update(sql, param);
    }

}
