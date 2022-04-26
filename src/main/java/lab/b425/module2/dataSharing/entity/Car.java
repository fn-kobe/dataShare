package lab.b425.module2.dataSharing.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
@ApiModel(value="Car对象", description="")
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "汽车id 用于唯一标识")
    @TableId
    private String carId;

    @ApiModelProperty(value = "车辆识别号")
    private String vinCode;

    @ApiModelProperty(value = "汽车制造商")
    private String make;

    @ApiModelProperty(value = "拥有者姓名")
    private String ownerName;

    @ApiModelProperty(value = "拥有者电话")
    private String ownerPhone;

    @ApiModelProperty(value = "所属通道")
    private String channel;


}
