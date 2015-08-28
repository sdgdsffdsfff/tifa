package walker.basewf.demo.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import walker.basewf.common.BasicController;
import walker.basewf.demo.form.LoginForm;
import walker.basewf.demo.service.BookService;

import java.util.Map;

/**
 * Created by HuQingmiao on 2015/3/4.
 */
@Controller
@RequestMapping("/login")
public class LoginController extends BasicController{

    @Autowired
    private BookService bookService;

    @RequestMapping
    public String login(@ModelAttribute("form") LoginForm form, Map<String, Object> map) {
        log.info(">>> login()");

        String username = form.getUsername();
        String password = form.getPassword();
        map.put("form",form);

        log.info("username: "+username);
        log.info("password: "+password);

        map.put("bookList", bookService.findBooks());
        return "listBook";
    }
}
