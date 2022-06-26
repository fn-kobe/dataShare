package lab.b425.module2.dataSharing.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lab.b425.module2.dataSharing.entity.Authority;
import lab.b425.module2.dataSharing.entity.ErrorEntity;
import lab.b425.module2.dataSharing.entity.ResponseEntity;
import lab.b425.module2.dataSharing.mapper.AuthorityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 权限管理服务类
 * </p>
 *
 * @author MFL
 * @since 2022-04-17
 */
@Service
public class AuthorityService {
    /**
     * Mapper及HTTP相关注册
     */
    private AuthorityMapper authorityMapper;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Autowired
    private void setAuthorityMapper(AuthorityMapper authorityMapper) {
        this.authorityMapper = authorityMapper;
    }

    @Autowired
    private void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Autowired
    private void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Transactional
    public String allocAuthority(String gainOrg,
                                 String targetOrg,
                                 String targetTable,
                                 Integer timePeriod,
                                 String authorityType) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        //判断输入是否合法
        //时间输入
        if (timePeriod < 1) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "时间范围非法", ErrorEntity.INVALID_ARGUMENT));
        }
        //目标表格输入
        if (!(targetTable.equals("purchase") ||
                targetTable.equals("sale") ||
                targetTable.equals("material") ||
                targetTable.equals("transaction"))) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "目标表格非法", ErrorEntity.INVALID_ARGUMENT));
        }
        //权限类型非法
        if (!(authorityType.equals("admin") || authorityType.equals("share"))) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "权限类型非法", ErrorEntity.INVALID_ARGUMENT));
        }
        //授予自身权限错误
        if (targetOrg.equals(gainOrg)) {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "用户不能授予自身权限", ErrorEntity.INVALID_ARGUMENT));
        }


        //授予权限之前 判断是否是授出自身权限
        if (!targetOrg.equals(loginOrg)) {
            //如果是授出的他人的权限，需要判断管理员身份和过期时间
            QueryWrapper<Authority> wrapper = new QueryWrapper<>();
            wrapper.eq("target_org", targetOrg)
                    .eq("org_id", loginOrg)
                    .eq("authority_type", "admin");
            Authority authority = authorityMapper.selectOne(wrapper);
            //判断管理员权限是否存在
            if (authority == null) {
                response.setStatus(400);
                return JSON.toJSONString(new ErrorEntity(400, "用户没有相关权限", ErrorEntity.INVALID_ARGUMENT));
            }
            //权限存在时
            else {
                //判断权限是否过期
                if (new Date().compareTo(authority.getExpirationTime()) >= 0) {
                    //删除过期权限
                    authorityMapper.deleteById(authority);
                    response.setStatus(400);
                    return JSON.toJSONString(new ErrorEntity(400, "用户没有相关权限", ErrorEntity.INVALID_ARGUMENT));
                }
                //权限未过期时
                else {
                    //判断授予权限类型是否非法
                    if (authorityType.equals("admin")) {
                        response.setStatus(400);
                        return JSON.toJSONString(new ErrorEntity(400, "管理员用户只能授出共享权限", ErrorEntity.INVALID_ARGUMENT));
                    }
                    //判断授予时间是否非法
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar instance = Calendar.getInstance();
                    instance.setTime(new Date());
                    instance.add(Calendar.DATE, timePeriod);
                    Date expireTime = instance.getTime();
                    if (expireTime.compareTo(authority.getExpirationTime()) >= 0) {
                        response.setStatus(400);
                        return JSON.toJSONString(new ErrorEntity(400, "管理员时间过期", ErrorEntity.INVALID_ARGUMENT));
                    }
                    //全部合法时， 管理员授予共享权限
                    Authority newAuthority = new Authority();
                    newAuthority.setAuthorityType("sharer");
                    newAuthority.setCreatorId(loginOrg);
                    newAuthority.setExpirationTime(expireTime);
                    newAuthority.setTargetOrg(targetOrg);
                    newAuthority.setTargetTable(targetTable);
                    newAuthority.setOrgId(gainOrg);
                    authorityMapper.insert(newAuthority);
                    return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "管理员授予权限成功"));
                }
            }
        }
        //当用户处理自身权限时

        //授予权限之前需要覆盖之前的同类型权限
        QueryWrapper<Authority> wrapper = new QueryWrapper<>();
        wrapper.eq("org_id", gainOrg)
                .eq("target_org", targetOrg)
                .eq("target_table", targetTable)
                .eq("creator_id", loginOrg);
        List<Authority> authorityList = authorityMapper.selectList(wrapper);
        for (Authority authority : authorityList) {
            authorityMapper.deleteById(authority);
        }

        Authority authority = new Authority();
        authority.setAuthorityType(authorityType);
        authority.setTargetTable(targetTable);
        authority.setOrgId(gainOrg);
        authority.setTargetOrg(loginOrg);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.DATE, timePeriod);
        Date expireTime = instance.getTime();
        authority.setExpirationTime(expireTime);
        authority.setCreatorId(loginOrg);
        authorityMapper.insert(authority);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "授予权限成功"));
    }

    @Transactional
    public String queryGainedAuthority() {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        String channel = (String) request.getSession().getAttribute("channel");
        QueryWrapper<Authority> wrapper = new QueryWrapper<>();
        wrapper.eq("org_id", loginOrg);
        List<Authority> authorityList = authorityMapper.selectList(wrapper);

        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询获取权限成功", authorityList));
    }

    @Transactional
    public String queryAuthorityToOthers() {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        QueryWrapper<Authority> wrapper = new QueryWrapper<>();
        wrapper.eq("target_org", loginOrg);
        List<Authority> authorityList = authorityMapper.selectList(wrapper);
        return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "查询授出权限成功", authorityList));
    }

    @Transactional
    public String removeAuthorityToOthers(String authorityId) {
        String loginOrg = (String) request.getSession().getAttribute("loginOrg");
        Authority removingAuthority = authorityMapper.selectById(authorityId);
        if (removingAuthority.getTargetOrg().equals(loginOrg)) {
            authorityMapper.deleteById(authorityId);
            return JSON.toJSONString(new ResponseEntity(ResponseEntity.OK, "移除权限成功"));
        } else {
            response.setStatus(400);
            return JSON.toJSONString(new ErrorEntity(400, "没有对应权限的管理资格", ErrorEntity.INVALID_ARGUMENT));
        }
    }

}
