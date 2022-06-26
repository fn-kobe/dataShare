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
@ApiModel(value="Repair对象", description="")
public class Repair implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId()
    private String repairId;

    @ApiModelProperty(value = "车辆识别码")
    private String vinCode;

    @ApiModelProperty(value = "信用码？")
    private String trustCode;

    @ApiModelProperty(value = "服务提供商编号")
    private String serviceProviderCode;

    @ApiModelProperty(value = "车辆牌照")
    private String licensePlate;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "日期")
    private Date datetime;

    @ApiModelProperty(value = "记录拥有者")
    private String uploader;

    @ApiModelProperty(value = "所属通道")
    private String channel;


}
