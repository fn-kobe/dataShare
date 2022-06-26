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
@ApiModel(value="Settlement对象", description="")
public class Settlement implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId
    private String settlementId;

    @ApiModelProperty(value = "购入物料的purchase_code，用于查询对应的物料")
    private String purchaseCode;

    @ApiModelProperty(value = "购入物料的material_code，用于查询对应的物料")
    private String materialCode;

    @ApiModelProperty(value = "结算方式")
    private String settlementMethod;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结算日期")
    private Date settlementDatetime;

    @ApiModelProperty(value = "记录拥有者")
    private String uploader;

    @ApiModelProperty(value = "所属通道")
    private String channel;


}
