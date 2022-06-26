package lab.b425.module2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * swagger配置类
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    //配置swagger的docket实例,可以有多个docket分组用以区分不同接口
    @Bean
    public Docket docket(Environment environment) {
        //设置使用swagger的环境
        Profiles profiles = Profiles.of("dev");

        //通过environment判断自己处在的环境中
        boolean flag = environment.acceptsProfiles(profiles);

        //设置docket分组
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(flag)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .groupName("DataShare")
                .select().apis(RequestHandlerSelectors.basePackage("lab.b425.module2")).build();
    }

    //配置apiInfo信息
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "基于区块链的多链业务协同构件",
                "数据共享模块",
                "1.0",
                "urn:tos",
                //作者信息
                new Contact("MFL", "", "471843059@qq.com"),
                "返回上一级模块选择",
                "http://192.168.1.103:8080/JavaCharts_war_exploded/index.jsp",
                new ArrayList<>()
        );
    }
}
