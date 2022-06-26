package lab.b425.module2.dataSharing.entity;

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
@ApiModel(value="User对象", description="")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    @TableId()
    private String id;

    @ApiModelProperty(value = "用户名称")
    private String name;

    @ApiModelProperty(value = "登录密码")
    private String password;

    @ApiModelProperty(value = "所属节点id")
    private String peerId;

    @ApiModelProperty(value = "用户所在通道")
    private String channel;

    @ApiModelProperty(value = "描述信息")
    private String info;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

//    @ApiModelProperty(value = "逻辑删除")
//    private Integer deleted;
//
//    @ApiModelProperty(value = "乐观锁")
//    private Integer version;


}
