package basic.mvc.demo.example.dao;

import basic.mvc.demo.example.po.Example;
import basic.mvc.utility.BaseDao;
import org.springframework.stereotype.Repository;

@Repository("ExampleDao")
public class ExampleDao extends BaseDao<Example> {

}
