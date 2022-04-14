package lab.b425.module2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

//@Controller
public class LoginController {

//    @RequestMapping("/userLogin")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model){

        if(!StringUtils.isEmpty(username)&&"123456".equals(password)){
            return "index";
        }else {
            model.addAttribute("msg","用户名或者密码错误！");
            return "login";
        }
    }

}
