package lab.b425.module2.dataSharing.service;

import com.alibaba.fastjson.JSON;
import lab.b425.module2.dataSharing.entity.ErrorEntity;
import lab.b425.module2.dataSharing.entity.Peer;
import lab.b425.module2.dataSharing.entity.ResponseEntity;
import lab.b425.module2.dataSharing.entity.User;
import lab.b425.module2.dataSharing.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * 用户管理服务类
 * </p>
 *
 * @author MFL
 * @since 2022-04-17
 */
@Service
public class UserService {
    /**
     * Mapper及HTTP相关注册
     */

    private PeerMapper peerMapper;

    private UserMapper userMapper;

    private HttpServletRequest request;

    private HttpServletResponse response;

    @Autowired
    private void setPeerMapper(PeerMapper peerMapper) {
        this.peerMapper = peerMapper;
    }

    @Autowired
    private void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Autowired
    private void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    private void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    /**
     * 接口代码部分,controller的实现类，
     * 详细接口注释参见controller
     */

    @Transactional
    public String signup(String id,
                         String name,
                         String peerId,
                         String password,
                         String channel,
                         String info) {

        /*
        检查id是否重复,或者节点是否存在
         */
        if (userMapper.selectById(id) != null || peerMapper.selectById(peerId) == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "用户已存在", ErrorEntity.INVALID_ARGUMENT));
        } else {
            User user = new User();
            user.setId(id);
            user.setName(name);
            user.setPeerId(peerId);
            user.setPassword(password);
            user.setChannel(channel);
            user.setInfo(info);
            userMapper.insert(user);
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "注册成功"));
    }


    public String login(String id,
                        String password,
                        HttpSession session,
                        HttpServletResponse response) {
        User thisUser = userMapper.selectById(id);
        if (thisUser == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "用户不存在", ErrorEntity.INVALID_ARGUMENT));
        }
        if (thisUser.getPassword().equals(password)) {
            Peer peer = peerMapper.selectById(thisUser.getPeerId());
            session.setAttribute("loginUser", id);
            session.setAttribute("loginOrg", peer.getOrganizationId());
            session.setAttribute("channel", thisUser.getChannel());
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "登录成功"));
        } else {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "用户id或密码错误", ErrorEntity.INVALID_ARGUMENT));
        }
    }

    public String logout(HttpSession session) {
        session.removeAttribute("loginUser");
        session.removeAttribute("loginOrg");
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "注销成功"));
    }

    @Transactional
    public String deleteUser(String id,
                             String password) {
        if (userMapper.selectById(id).getPassword().equals(password)) {
            userMapper.deleteById(id);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "删除成功"));
        } else {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "用户id或密码错误", ErrorEntity.INVALID_ARGUMENT));
        }
    }

    @Transactional
    public String updateUser(String name,
                             String password,
                             String info) {
        String id = (String) request.getSession().getAttribute("loginUser");
        if (userMapper.selectById(id) == null) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "用户不存在", ErrorEntity.INVALID_ARGUMENT));
        } else {
            User user = userMapper.selectById(id);
            user.setName(name);
            user.setPassword(password);
            user.setInfo(info);
            userMapper.updateById(user);
        }
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "更新成功"));
    }
}
