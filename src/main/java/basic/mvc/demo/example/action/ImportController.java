package basic.mvc.demo.example.action;

import basic.mvc.utility.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/data-import")
public class ImportController extends BaseController {
}
