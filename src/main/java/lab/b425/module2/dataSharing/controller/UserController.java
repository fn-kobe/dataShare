package lab.b425.module2.dataSharing.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lab.b425.module2.dataSharing.entity.ErrorEntity;
import lab.b425.module2.dataSharing.service.DataQueryService;
import lab.b425.module2.dataSharing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 路由类 权限
 */
@RestController
@Api(tags = "用户管理")
public class UserController {

    /**
     * 注意！ 使用@Autowired的类本身也必须通过自动装配生成，否则会导致注入为空
     */

    private UserService userService;
    private DataQueryService dataQueryService;
    private HttpServletResponse response;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDataQueryService(DataQueryService dataQueryService) {
        this.dataQueryService = dataQueryService;
    }

    @Autowired
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    @ApiOperation(value = "用户注册", notes = "用户根据已经配置好的节点和组织信息进行注册，每个用户需要指定通道信息")
    @RequestMapping(value = "/user", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String signup(@RequestParam @ApiParam("用户id") String id,
                         @RequestParam @ApiParam("用户名称") String name,
                         @RequestParam @ApiParam("节点id") String peerId,
                         @RequestParam @ApiParam("用户密码") String password,
                         @RequestParam @ApiParam("所在通道") String channel,
                         @RequestParam(defaultValue = "") @ApiParam("描述") String info) {

        return userService.signup(id, name, peerId, password, channel, info);
    }

    @ApiOperation(value = "用户登录", notes = "用户使用账号密码完成登录，代表所在的组织执行相关业务")
    @RequestMapping(value = "/user/login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String login(@RequestParam @ApiParam("用户id") String id,
                        @RequestParam @ApiParam("用户密码") String password,
                        @ApiIgnore HttpSession session,
                        @ApiIgnore HttpServletResponse response) {

        return userService.login(id, password, session, response);
    }

    @ApiOperation(value = "用户注销", notes = "用户退出登录")
    @RequestMapping(value = "/user/logout", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String logout(@ApiIgnore HttpSession session) {
        return userService.logout(session);
    }

    @ApiOperation(value = "删除用户", notes = "删除当前用户")
    @RequestMapping(value = "/user", method = RequestMethod.DELETE, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteUser(@RequestParam @ApiParam("用户id") String id,
                             @RequestParam @ApiParam("用户密码") String password) {

        return userService.deleteUser(id, password);
    }

    @ApiIgnore("更新用户")
    @RequestMapping(value = "/user", method = RequestMethod.PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateUser(@RequestParam @ApiParam("用户名称") String name,
                             @RequestParam @ApiParam("用户密码") String password,
                             @RequestParam(defaultValue = "") @ApiParam("描述") String info) {

        return userService.updateUser(name, password, info);
    }

    @ApiIgnore
    @RequestMapping(value = "/error/un_log", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String unLog() {
        response.setStatus(401);
        return JSON.toJSONString(new ErrorEntity(401, "用户尚未登录", ErrorEntity.UNAUTHORIZED));
    }

    @ApiOperation("重置环境")
    @RequestMapping(value = "/resetAll", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String resetAll() {
        return dataQueryService.resetAll();
    }


}
