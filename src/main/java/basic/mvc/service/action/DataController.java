package basic.mvc.service.action;

import basic.mvc.utility.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/service/data")
public class DataController extends BaseController {
}
