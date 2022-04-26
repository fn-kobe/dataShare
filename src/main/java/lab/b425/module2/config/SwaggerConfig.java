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
                .groupName("MFL")
                .select().apis(RequestHandlerSelectors.basePackage("lab.b425.module2")).build();
    }

    //配置apiInfo信息
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Api Documentation",
                "Api Documentation",
                "1.0",
                "urn:tos",
                //作者信息
                new Contact("MFL", "", "471843059@qq.com"),
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>()
        );
    }
}
