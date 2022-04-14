package lab.b425.module2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * springMVC配置类
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    //在配置类中注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginHandlerInterceptor())
                .addPathPatterns("/addAsset","/allocRole","/rewriteChTime","/checkRole","/addAuthorityToRole","/createRole"); //实际应该将所有用户接口加入拦截，系统内部接口直接放行
    }

    //视图跳转

    /*@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        WebMvcConfigurer.super.addViewControllers(registry);
        registry.addViewController("/main").setViewName("login");
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/index.html").setViewName("login");
    }*/
}
