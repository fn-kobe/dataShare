package lab.b425.module2.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@ApiModel(value="UserAssetRole对象", description="")
public class UserAssetRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "拥有此权限的角色id")
    private String roleId;

    @ApiModelProperty(value = "权限可以对应哪一组织的资产")
    private String targetOrg;

    @ApiModelProperty(value = "资产类型")
    private String assetType;

    @ApiModelProperty(value = "权限范围")
    private String authority;

    @ApiModelProperty(value = "过期时间")
    private Date expirationTime;

    @ApiModelProperty(value = "创建者id")
    private String creatorId;

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
