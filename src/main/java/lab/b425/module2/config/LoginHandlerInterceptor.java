package lab.b425.module2.config;

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
            //request.getRequestDispatcher("/error/un_log").forward(request,response); //页面重定向
            response.sendRedirect("/error/un_log");
            return false;
        } else {
            return true;
        }
    }
}
