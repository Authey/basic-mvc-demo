package basic.mvc.demo.example.service.impl;

import basic.mvc.demo.example.service.ExampleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ExampleServiceImpl")
@Transactional
public class ExampleServiceImpl implements ExampleService {
}
