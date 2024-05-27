package basic.mvc.demo.example.action;

import basic.mvc.utility.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/clob-concat")
public class ConcatController extends BaseController {
}
