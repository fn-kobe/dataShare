package lab.b425.module2.dataSharing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lab.b425.module2.dataSharing.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "权限管理")
public class AuthorityController {

    private AuthorityService authorityService;
    @Autowired
    public  void  setAuthorityService(AuthorityService authorityService){
        this.authorityService = authorityService;
    }

    @ApiOperation(value = "添加权限", notes = "向其他组织授出自身相关数据的权限，或者作为其他组织授予的管理员身份授出对应的管理权限")
    @RequestMapping(value = "/allocAuthority", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String allocAuthority(@RequestParam @ApiParam("获取权限组织") String gainOrg,
                                 @RequestParam @ApiParam("授出权限组织") String targetOrg,
                                 @RequestParam @ApiParam("授出权限的数据类型") String targetTable,
                                 @RequestParam @ApiParam("权限有效时间，单位为天") Integer timePeriod,
                                 @RequestParam @ApiParam("权限类别") String authorityType)  {
        return authorityService.allocAuthority(gainOrg, targetOrg, targetTable, timePeriod, authorityType);
    }

    @ApiOperation(value = "获取被授予的权限", notes = "获取本组织所有被授予权限")
    @RequestMapping(value = "/queryGainedAuthority", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryGainedAuthority() {
        return authorityService.queryGainedAuthority();
    }

    @ApiOperation(value = "获取已授出的权限", notes = "获取本组织所有授出的权限")
    @RequestMapping(value = "/queryAuthorityToOthers", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String queryAuthorityToOthers() {
        return authorityService.queryAuthorityToOthers();
    }

    @ApiOperation(value = "移除权限", notes = "移除本组织授出的权限")
    @RequestMapping(value = "/removeAuthorityToOthers", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String removeAuthorityToOthers(@RequestParam @ApiParam(value = "权限id", required = true) String authorityId) {
        return authorityService.removeAuthorityToOthers(authorityId);
    }
}
