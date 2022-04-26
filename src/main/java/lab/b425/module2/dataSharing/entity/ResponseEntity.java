package lab.b425.module2.dataSharing.entity;

public class ResponseEntity {
    /**
     * 200 请求成功。一般用于GET与POST请求
     * 201 	已创建。成功请求并创建了新的资源
     */
    public static Integer OK = 200;
    public static Integer CREATE = 201;

    /**
     * 400 客户端请求的语法错误，服务器无法理解
     * 401 请求要求用户的身份认证
     * 403 服务器理解请求客户端的请求，但是拒绝执行此请求
     */
    public static Integer BAD_REQUEST = 400;
    public static Integer UNAUTHORIZED = 401;
    public static Integer FORBIDDEN = 403;

    /**
     * 500 服务器内部错误，无法完成请求
     * 502 作为网关或者代理工作的服务器尝试执行请求时，从远程服务器接收到了一个无效的响应
     * 503 由于超载或系统维护，服务器暂时的无法处理客户端的请求。延时的长度可包含在服务器的Retry-After头信息中
     */
    public static Integer INTERNAL_SERVER_ERROR = 500;
    public static Integer BAD_GATEWAY = 502;
    public static Integer SERVICE_UNAVAILABLE = 503;


    public Integer code;
    public String message;
    public Object data;

    public ResponseEntity() {
    }

    public ResponseEntity(Integer code) {
        this.code = code;
    }

    public ResponseEntity(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseEntity(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseEntity setCode(Integer code) {
        this.code = code;
        return this;
    }

    public ResponseEntity setMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseEntity setData(Object data) {
        this.data = data;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
