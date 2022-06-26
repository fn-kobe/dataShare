package lab.b425.module2.dataSharing.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 对应数据库表格的实体类
 * </p>
 *
 * @author MFL
 * @since 2022-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Transaction对象", description="")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId()
    private String transactionId;

    private String sender;

    private String receiver;

    private String assetCode;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date datetime;

    private Double price;

    private String state;

    private String channel;


}
