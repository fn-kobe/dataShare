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
@ApiModel(value="Asset对象", description="")
public class Asset implements Serializable {

    public Asset() {
    }

    public Asset(Asset oldOne) {
        this.id=oldOne.id;
        this.type=oldOne.type;
        this.state=oldOne.state;
        this.componentOf=oldOne.componentOf;
        this.component=oldOne.component;
        this.quantity=oldOne.quantity;
        this.value=oldOne.value;
        this.holderId=oldOne.holderId;
        this.exHolderId=oldOne.exHolderId;
        this.producerId=oldOne.producerId;
        this.producedDate=oldOne.producedDate;
        this.description=oldOne.description;
        this.warehousePosition=oldOne.warehousePosition;
    }

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 资产id")
    private String id;

    @ApiModelProperty(value = "资产类型，原材料.零部件.系统.整车")
    private String type;

    @ApiModelProperty(value = "资产的状态例如，库存、废弃、已使用、物流")
    private String state;

    @ApiModelProperty(value = "资产的去向，成为其他资产的组成部分，或者成为整车")
    private String componentOf;

    @ApiModelProperty(value = "资产的组成部分")
    private String component;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "价格")
    private Double value;

    @ApiModelProperty(value = "拥有组织id")
    private String holderId;

    @ApiModelProperty(value = "上一拥有组织id")
    private String exHolderId;

    @ApiModelProperty(value = "生产组织id")
    private String producerId;

    @ApiModelProperty(value = "生产日期")
    private Date producedDate;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "仓储位置")
    private String warehousePosition;

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
