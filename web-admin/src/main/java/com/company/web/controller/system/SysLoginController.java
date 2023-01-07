package com.company.web.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.company.common.constant.Constants;
import com.company.common.core.domain.R;
import com.company.common.core.domain.entity.SysMenu;
import com.company.common.core.domain.entity.SysUser;
import com.company.common.core.domain.model.LoginBody;
import com.company.common.core.domain.model.LoginUser;
import com.company.common.utils.ServletUtils;
import com.company.framework.web.service.SysLoginService;
import com.company.framework.web.service.SysPermissionService;
import com.company.framework.web.service.TokenService;
import com.company.system.service.ISysMenuService;

/**
 * 登录验证
 */
@RestController
public class SysLoginController {

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public R login(@RequestBody LoginBody loginBody) {
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        return R.successWithData(new HashMap(2){{
            put(Constants.TOKEN, token);
        }});
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public R getInfo() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        return R.successWithData(new HashMap(2){{
            put("user", user);
            put("roles", roles);
            put("permissions", permissions);
        }});
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public R getRouters() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        // 用户信息
        SysUser user = loginUser.getUser();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(user.getUserId());
        return R.successWithData(menuService.buildMenus(menus));
    }
}
