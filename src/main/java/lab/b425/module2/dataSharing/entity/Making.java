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
@ApiModel(value="Making对象", description="")
public class Making implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "制造记录id")
    @TableId()
    private String makingId;

    @ApiModelProperty(value = "所消耗物料的purchase_code，用于查询对应的物料")
    private String purchaseCode;

    @ApiModelProperty(value = "所消耗物料的material_code，用于查询对应的物料")
    private String materialCode;

    @ApiModelProperty(value = "所消耗物料的数量")
    private Integer costNumber;

    @ApiModelProperty(value = "生产出的汽车的车辆识别码")
    private String vinCode;

    @ApiModelProperty(value = "品牌型号")
    private String make;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "上传时间")
    private Date datetime;

    @ApiModelProperty(value = "记录者拥有者")
    private String uploader;

    @ApiModelProperty(value = "频道")
    private String channel;


}
