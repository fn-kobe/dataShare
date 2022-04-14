package lab.b425.module2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
public class IndexController {
//    @RequestMapping("index")
    public String test() {
        return "index";
    }
}
