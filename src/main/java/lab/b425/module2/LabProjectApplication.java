package lab.b425.module2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("lab.b425.module2.dataSharing.mapper")
public class LabProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabProjectApplication.class, args);
    }

}
