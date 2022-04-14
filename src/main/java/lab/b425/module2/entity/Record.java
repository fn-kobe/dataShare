package lab.b425.module2.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
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
@ApiModel(value="Record对象", description="")
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id 主键")
    private String id;

    @ApiModelProperty(value = "记录原文件")
    private String record;

    @ApiModelProperty(value = "记录类型")
    private String recordType;

    @ApiModelProperty(value = "操作发起者")
    private String operator;

    @ApiModelProperty(value = "操作背书者")
    private String endorser;

    @ApiModelProperty(value = "记录状态，0 未审批， 1 已通过 2 未通过")
    private String state;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "逻辑删除")
    @TableField(exist = false)
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "乐观锁")
    @TableField(exist = false)
    @Version
    private Integer version;


}
