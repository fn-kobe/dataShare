package lab.b425.module2.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author MFL
 * @since 2021-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="区块链操作记录日志", description="")
public class OperationLog {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id 主键")
    @TableId(type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "事务id")
    private String transactionId;

    @ApiModelProperty(value = "记录已完成的区块链或者事务")
    private String state;

    @ApiModelProperty(value = "对应的交易请求记录")
    private String recordId;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
