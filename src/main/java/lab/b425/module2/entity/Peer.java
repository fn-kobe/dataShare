package lab.b425.module2.entity;

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
 * @since 2021-11-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Peer对象", description="")
public class Peer implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "节点id")
    private String id;

    @ApiModelProperty(value = "节点名称")
    private String name;

    @ApiModelProperty(value = "所属组织id")
    private String organizationId;

    @ApiModelProperty(value = "节点类别")
    private String type;


}
