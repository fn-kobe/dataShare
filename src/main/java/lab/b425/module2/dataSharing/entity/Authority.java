package lab.b425.module2.dataSharing.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
@ApiModel(value="Authority对象", description="")
public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId()
    private String authorityId;

    @ApiModelProperty(value = "获得权限的组织")
    private String orgId;

    @ApiModelProperty(value = "获得权限的组织")
    private String targetOrg;

    @ApiModelProperty(value = "权限的目标记录")
    private String targetTable;

    @ApiModelProperty(value = "权限类别")
    private String authorityType;

    @ApiModelProperty(value = "权限创建者")
    private String creatorId;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "过期时间")
    private Date expirationTime;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private String datetime;

    @ApiModelProperty(value = "更新日期")
    private String update_datetime;

    @ApiModelProperty(value = "逻辑删除")
    @TableField(exist = false)
    @TableLogic
    private Integer deleted;


}
