package lab.b425.module2.dataSharing.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2022-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Purchase对象", description="")
public class Purchase implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId()
    private String purchaseId;

    @ApiModelProperty(value = "购买编号")
    private String purchaseCode;

    @ApiModelProperty(value = "购入材料编号")
    private String materialCode;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "购入者")
    private String owner;

    @ApiModelProperty(value = "购入数量")
    private Integer purchaseNumber;

    @ApiModelProperty(value = "价格")
    private Double price;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "日期")
    private Date datetime;

    @ApiModelProperty(value = "记录拥有者")
    private String uploader;

    @ApiModelProperty(value = "所属频道")
    private String channel;


}
