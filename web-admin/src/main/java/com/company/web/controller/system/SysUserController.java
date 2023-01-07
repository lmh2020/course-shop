package com.company.web.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.company.common.core.domain.R;
import com.company.common.utils.bean.BeanTransUtil;
import com.company.system.domain.SysUserRole;
import com.company.system.form.UserRegisterForm;
import com.company.system.form.UserUpdateForm;
import com.company.system.mapper.SysUserMapper;
import com.company.system.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.company.common.annotation.Log;
import com.company.common.constant.UserConstants;
import com.company.common.core.controller.BaseController;
import com.company.common.core.domain.entity.SysRole;
import com.company.common.core.domain.entity.SysUser;
import com.company.common.core.domain.model.LoginUser;
import com.company.common.core.page.TableDataInfo;
import com.company.common.enums.BusinessType;
import com.company.common.utils.SecurityUtils;
import com.company.common.utils.ServletUtils;
import com.company.common.utils.StringUtils;
import com.company.common.utils.poi.ExcelUtil;
import com.company.framework.web.service.TokenService;
import com.company.system.service.ISysPostService;
import com.company.system.service.ISysRoleService;
import com.company.system.service.ISysUserService;

/**
 * 用户信息
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @GetMapping("/export")
    public R export(SysUser user) {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return R.successWithData(util.exportExcel(list, "用户数据"));
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public R importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        String operName = loginUser.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return R.success(message);
    }

    @GetMapping("/importTemplate")
    public R<String> importTemplate() {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return R.successWithData(util.importTemplateExcel("用户数据"));
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public R getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        List<SysRole> roles = roleService.selectRoleAll();
        return R.successWithData(new HashMap() {{
            put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
            put("posts", postService.selectPostAll());
            if (StringUtils.isNotNull(userId)) {
                put("user", userService.selectUserById(userId));
                put("postIds", postService.selectPostListByUserId(userId));
                put("roleIds", roleService.selectRoleListByUserId(userId));
            } else {
                put("user", userService.selectUserById(SecurityUtils.getLoginUserId()));
            }
        }});
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@Validated @RequestBody SysUser user) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return R.fail("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return doSuccess(userService.insertUser(user));
    }

    @PostMapping("register")
    @Transactional(rollbackFor = Exception.class)
    public R register(@RequestBody @Validated UserRegisterForm form){
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(form.getUserName()))) {
            return R.fail("新增用户'" + form.getUserName() + "'失败，登录账号已存在");
        } else if (userMapper.checkPhoneUnique(form.getPhonenumber()) != null) {
            return R.fail("新增用户'" + form.getUserName() + "'失败，手机号码已存在");
        }
        SysUser sysUser = new SysUser();
        sysUser.setPassword(SecurityUtils.encryptPassword(form.getPassword()));
        sysUser.setNickName(form.getNickName());
        sysUser.setPhonenumber(form.getPhonenumber());
        sysUser.setUserName(form.getUserName());
        sysUser.setStatus("0");
        sysUser.setAvatar("https://img1.baidu.com/it/u=2719292398,3645159946&fm=26&fmt=auto&gp=0.jpg");
        userMapper.insertUser(sysUser);

        List<SysUserRole> roleList = new ArrayList<>(1);
        SysUserRole ur = new SysUserRole();
        ur.setUserId(sysUser.getUserId());
        ur.setRoleId(2L);
        roleList.add(ur);
        userRoleMapper.batchUserRole(roleList);

        return R.successWithData(sysUser);
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        return doSuccess(userService.updateUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping("app")
    public R editApp(@Validated @RequestBody UserUpdateForm form) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        BeanTransUtil.copy(form, user);
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return R.fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(SecurityUtils.getUsername());
        userService.updateUserApp(user);
        return R.successWithData(user);
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R remove(@PathVariable Long[] userIds) {
        return doSuccess(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public R resetPwd(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return doSuccess(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return doSuccess(userService.updateUserStatus(user));
    }
}
