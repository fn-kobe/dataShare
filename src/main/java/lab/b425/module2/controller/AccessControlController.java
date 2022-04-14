package lab.b425.module2.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lab.b425.module2.entity.ResponseEntity;
import lab.b425.module2.service.AccessControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 路由类 权限
 */
@RestController
public class AccessControlController {

    /**
     * 注意！ 使用@Autowired的类本身也必须通过自动装配生成，否则会导致注入为空
     */
    private AccessControlService accessControlService;
//    private HttpServletRequest request;

    @Autowired
    public void setAccessControlService(AccessControlService accessControlService) {
        this.accessControlService = accessControlService;
    }

//    @ApiIgnore
//    @RequestMapping(value = "", method = RequestMethod.GET)
//    @ResponseBody
//    public String hello() {
//        return "hello";
//    }

    /*@RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test(@RequestParam(required = false) @ApiParam("输入字符串") String str,
                       @RequestParam(required = false) @ApiParam("输入数组") String... arr) {
        System.out.println(str == null);
        System.out.println(Arrays.toString(arr));
        return "hello";
    }*/

    @ApiOperation("用户注册")
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    public String signup(@RequestParam @ApiParam("用户id") String id,
                         @RequestParam @ApiParam("用户名称") String name,
                         @RequestParam @ApiParam("节点id") String peerId,
                         @RequestParam @ApiParam("用户密码") String password,
                         @RequestParam(defaultValue = "") @ApiParam("描述") String info) {

        return accessControlService.signup(id, name, peerId, password, info);
    }

    @ApiOperation("用户登录")
    @ApiResponses({
            @ApiResponse(code = 400, message = ""),
            @ApiResponse(code = 401,message = "401自定义描述")
    })
    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestParam @ApiParam("用户id") String id,
                        @RequestParam @ApiParam("用户密码") String password,
                        @ApiIgnore HttpSession session,
                        @ApiIgnore HttpServletResponse response) {

        return accessControlService.login(id, password, session, response);
    }

    @ApiOperation("用户注销")
    @RequestMapping(value = "/user/logout", method = RequestMethod.GET)
    @ResponseBody
    public String logout(@ApiIgnore HttpSession session) {
        return accessControlService.logout(session);
    }

    @ApiOperation("删除用户")
    @RequestMapping(value = "/user", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteUser(@RequestParam @ApiParam("用户id") String id,
                             @RequestParam @ApiParam("用户密码") String password) {

        return accessControlService.deleteUser(id, password);
    }

    @ApiIgnore("更新用户")
    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    @ResponseBody
    public String updateUser(@RequestParam @ApiParam("用户名称") String name,
                             @RequestParam @ApiParam("用户密码") String password,
                             @RequestParam(defaultValue = "") @ApiParam("描述") String info) {

        return accessControlService.updateUser(name, password, info);
    }


    @ApiOperation("为用户分配资产权限")
    @RequestMapping(value = "/allocAssetRole", method = RequestMethod.POST)
    @ResponseBody
    public String allocRole(@RequestParam @ApiParam("对应的资产类别") String assetType,
                            @RequestParam @ApiParam("角色类型") String roleType,
                            @RequestParam @ApiParam("拥有此权限的用户id") String userId,
                            @RequestParam @ApiParam("权限可以对应哪一组织的资产") String targetOrg,
                            @RequestParam(defaultValue = "30000", required = false) @ApiParam("角色有效时长，单位为天") Integer time,
                            @RequestParam(required = false) @ApiParam("所分配的权限，未包括此参数时开启所有权限") String... authority) {

        return accessControlService.allocAssetRole(assetType, roleType, userId, targetOrg, time, authority);
    }

    @ApiOperation("更新资产权限")
    @RequestMapping(value = "/updateAssetRole", method = RequestMethod.PUT)
    @ResponseBody
    public String updateRole(@RequestParam @ApiParam("对应的资产类型") String assetType,
                             @RequestParam @ApiParam("角色类型") String roleId,
                             @RequestParam @ApiParam("拥有此权限的用户id") String userId,
                             @RequestParam @ApiParam("权限可以对应哪一组织的资产") String targetOrg,
                             @RequestParam(defaultValue = "30000", required = false) @ApiParam("角色有效时长，单位为天") Integer time,
                             @RequestParam(required = false) @ApiParam("所分配的权限，未包括此参数时开启所有权限") String... authority) {

        return accessControlService.updateAssetRole(assetType, roleId, userId, targetOrg, time, authority);
    }

    @ApiOperation("为用户分配记录权限")
    @RequestMapping(value = "/allocRecordRole", method = RequestMethod.POST)
    @ResponseBody
    public String allocRecordRole(@RequestParam @ApiParam("对应的记录类型") String recordType,
                                  @RequestParam @ApiParam("角色类型") String roleType,
                                  @RequestParam @ApiParam("拥有此权限的用户id") String userId,
                                  @RequestParam @ApiParam("权限可以对应哪一组织的记录") String targetOrg,
                                  @RequestParam(defaultValue = "30000", required = false) @ApiParam("角色有效时长，单位为天") Integer time) {

        return accessControlService.allocRecordRole(recordType, roleType, userId, targetOrg, time);
    }

    @ApiOperation("更新记录权限")
    @RequestMapping(value = "/updateRecordRole", method = RequestMethod.PUT)
    @ResponseBody
    public String updateRecordRole(@RequestParam @ApiParam("对应的记录类型") String recordType,
                                   @RequestParam @ApiParam("角色类型") String roleType,
                                   @RequestParam @ApiParam("拥有此权限的用户id") String userId,
                                   @RequestParam @ApiParam("权限可以对应哪一组织的记录") String targetOrg,
                                   @RequestParam(defaultValue = "30000", required = false) @ApiParam("角色有效时长，单位为天") Integer time) {

        return accessControlService.updateRecordRole(recordType, roleType, userId, targetOrg, time);
    }

    @ApiOperation("对于登录用户，查询拥有的对于某一类别的资产权限")
    @RequestMapping(value = "/checkAssetRoleType", method = RequestMethod.GET)
    @ResponseBody
    public String checkAssetRoleType(@RequestParam @ApiParam("对应的资产类型") String assetType) {

        return accessControlService.checkAssetRoleType(assetType);
    }

    @ApiOperation("对于登录用户,查询对于某一组织拥有的资产权限")
    @RequestMapping(value = "/checkAssetRoleOrg", method = RequestMethod.GET)
    @ResponseBody
    public String checkAssetRoleOrg(@RequestParam @ApiParam("对应组织") String targetOrg) {

        return accessControlService.checkAssetRoleOrg(targetOrg);
    }

    @ApiOperation("对于登录用户,查询其组织所分配出的资产权限")
    @RequestMapping(value = "/queryAssetRoleForSelf", method = RequestMethod.GET)
    @ResponseBody
    public String queryAssetRoleForSelf() {

        return accessControlService.queryAssetRoleForSelf();
    }

    @ApiOperation("对于登录用户，查询拥有的对于某一类别的记录权限")
    @RequestMapping(value = "/checkRecordTypeRole", method = RequestMethod.GET)
    @ResponseBody
    public String checkRecordTypeRole(@RequestParam @ApiParam("对应的记录类型") String recordType) {

        return accessControlService.checkRecordTypeRole(recordType);
    }

    @ApiOperation("对于登录用户,查询对于某一组织拥有的记录权限")
    @RequestMapping(value = "/checkRecordTypeOrg", method = RequestMethod.GET)
    @ResponseBody
    public String checkRecordTypeOrg(@RequestParam @ApiParam("对应组织") String targetOrg) {

        return accessControlService.checkRecordTypeOrg(targetOrg);
    }

    @ApiOperation("对于登录用户,查询其组织所分配出的记录权限")
    @RequestMapping(value = "/queryRecordRoleForSelf", method = RequestMethod.GET)
    @ResponseBody
    public String queryRecordRoleForSelf() {

        return accessControlService.queryRecordRoleForSelf();
    }

    @ApiOperation("对于登录用户,移除一笔资产权限")
    @RequestMapping(value = "/deleteAssetRole", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteAssetRole(String assetRoleId){

        return accessControlService.deleteAssetRole(assetRoleId);
    }

    @ApiOperation("对于登录用户,移除一笔记录权限")
    @RequestMapping(value = "/deleteRecordRole", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteRecordRole(String recordRoleId){

        return accessControlService.deleteRecordRole(recordRoleId);
    }

    @ApiOperation("当前登录的用户和组织")
    @RequestMapping(value = "/checkLogin",method = RequestMethod.GET)
    @ResponseBody
    public String checkLogin(@ApiIgnore HttpSession session) {

        return accessControlService.checkLogin(session);
    }

    @ApiIgnore
    @RequestMapping("/error/un_log")
    @ResponseBody
    public String unLog() {
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.UNAUTHORIZED, "用户尚未登录"));
    }


}
