package lab.b425.module2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AOP登录验证处理器
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //登录成功后获取用户的session
        Object loginUser = request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
//            request.setAttribute("message", "权限不足或尚未登录");
//            request.getRequestDispatcher("/error/un_log").forward(request,response); //重定向页面
            response.sendRedirect("/error/un_log");
            return false;
        } else {
            return true;
        }
    }
}
