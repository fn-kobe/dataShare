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
@ApiModel(value="Asset对象", description="")
public class Asset implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId()
    private String assetId;

    @ApiModelProperty(value = "资产编号")
    private String assetCode;

    @ApiModelProperty(value = "资产的品牌型号")
    private String make;

    @ApiModelProperty(value = "资产数量")
    private Integer number;

    @ApiModelProperty(value = "资产持有者")
    private String owner;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "所属通道")
    private String channel;


}
