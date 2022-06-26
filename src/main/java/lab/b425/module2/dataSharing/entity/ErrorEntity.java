package lab.b425.module2.dataSharing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>
 * 返回错误信息的实体类
 * </p>
 *
 * @author MFL
 * @since 2022-04-17
 */
@Data
public class ErrorEntity {
    public ErrorBody error;

    //参数非法
    public static String INVALID_ARGUMENT = "INVALID_ARGUMENT";
    //路径错误
    public static String DOMAIN_NAME_NOT_FOUND = "DOMAIN_NAME_NOT_FOUND";
    //权限错误
    public static String UNAUTHORIZED = "UNAUTHORIZED";
    //服务器内部错误
    public static String INTERNAL_SERVICE_ERROR = "INTERNAL_SERVICE_ERROR";

    public ErrorEntity(Integer code, String message, String status) {
        this.error = new ErrorBody(code, message, status);
    }

}

@Data
@AllArgsConstructor
class ErrorBody{
    Integer code;
    String message;
    String status;
}
