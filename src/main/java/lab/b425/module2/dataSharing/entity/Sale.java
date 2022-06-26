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
 * 对应数据库表格的实体类
 * </p>
 *
 * @author MFL
 * @since 2022-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Sale对象", description="")
public class Sale implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId()
    private String saleId;

    @ApiModelProperty(value = "汽车识别号")
    private String vinCode;

    @ApiModelProperty(value = "销售编号")
    private String saleCode;

    @ApiModelProperty(value = "客户编号")
    private String clientCode;

    @ApiModelProperty(value = "经销商编号")
    private String dealerCode;

    @ApiModelProperty(value = "价格")
    private Double price;

    @ApiModelProperty(value = "购买者姓名")
    private String ownerName;

    @ApiModelProperty(value = "购买者电话")
    private String ownerPhone;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "日期")
    private Date datetime;

    @ApiModelProperty(value = "记录拥有者")
    private String uploader;

    @ApiModelProperty(value = "所属通道")
    private String channel;


}
