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
@ApiModel(value="Material对象", description="")
public class Material implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId()
    private String materialId;

    @ApiModelProperty(value = "购入编号")
    private String purchaseCode;

    @ApiModelProperty(value = "材料编号")
    private String materialCode;

    @ApiModelProperty(value = "材料描述")
    private String description;

    @ApiModelProperty(value = "材料的持有者")
    private String owner;

    @ApiModelProperty(value = "购入数量")
    private Integer purchaseNumber;

    @ApiModelProperty(value = "当前数量")
    private Integer currentNumber;

    @ApiModelProperty(value = "价格")
    private Double price;

    @ApiModelProperty(value = "结算方式")
    private String settlementMethod;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结算日期")
    private Date settlementDatetime;

    @ApiModelProperty(value = "存储容器？")
    private String warehouseReservoir;

    @ApiModelProperty(value = "存储位置")
    private String warehousePosition;

    @ApiModelProperty(value = "记录拥有者")
    private String uploader;

    @ApiModelProperty(value = "所属通道")
    private String channel;


}
