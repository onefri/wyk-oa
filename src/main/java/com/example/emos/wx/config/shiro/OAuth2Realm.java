package com.example.emos.wx.config.shiro;

import com.example.emos.wx.db.pojo.TbUser;
import com.example.emos.wx.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 定义认证与授权的的实现方法
 */
@Component
public class OAuth2Realm extends AuthorizingRealm {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof OAuth2Token;
    }


    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String accessToken  = (String) token.getPrincipal();

        int userId = jwtUtil.getUserId(accessToken);

        TbUser tbUser = userService.searchById(userId);
        if (tbUser==null){
            throw  new LockedAccountException("账号已被锁定，请联系管理员！");
        }

        //存储认证信息
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(tbUser,accessToken,getName());

        return info;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection collection) {

        TbUser user = (TbUser) collection.getPrimaryPrincipal();
        Integer id = (Integer) user.getId();
        Set<String> permissions = userService.searchUserPermissions(id);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permissions);
        return info;
    }

}
