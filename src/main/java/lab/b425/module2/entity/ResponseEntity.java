package lab.b425.module2.entity;

public class ResponseEntity {
    /**
     * 200 请求成功。一般用于GET与POST请求
     * 201 	已创建。成功请求并创建了新的资源
     */
    public static String OK = "200";
    public static String CREATE = "201";

    /**
     * 400 客户端请求的语法错误，服务器无法理解
     * 401 请求要求用户的身份认证
     * 403 服务器理解请求客户端的请求，但是拒绝执行此请求
     */
    public static String BAD_REQUEST = "400";
    public static String UNAUTHORIZED = "401";
    public static String FORBIDDEN = "403";

    /**
     * 500 服务器内部错误，无法完成请求
     * 502 作为网关或者代理工作的服务器尝试执行请求时，从远程服务器接收到了一个无效的响应
     * 503 由于超载或系统维护，服务器暂时的无法处理客户端的请求。延时的长度可包含在服务器的Retry-After头信息中
     */
    public static String INTERNAL_SERVER_ERROR = "500";
    public static String BAD_GATEWAY = "502";
    public static String SERVICE_UNAVAILABLE = "503";


    public String code;
    public String message;
    public String data;

    public ResponseEntity() {
    }

    public ResponseEntity(String code) {
        this.code = code;
    }

    public ResponseEntity(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseEntity(String code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseEntity setCode(String code) {
        this.code = code;
        return this;
    }

    public ResponseEntity setMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseEntity setData(String data) {
        this.data = data;
        return this;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}
