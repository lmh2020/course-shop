package com.company.common.core.mybaits;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.company.common.core.domain.model.LoginUser;
import com.company.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Description  mybaits-plus  新增或修改时，公共字段自动填充
 */
@Component
public class EntityFieldHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        LoginUser loginUser;
        Authentication authentication = SecurityUtils.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser &&
                (loginUser = (LoginUser)authentication.getPrincipal()) != null){
            this.strictInsertFill(metaObject, "createBy", String.class, loginUser.getUsername());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        LoginUser loginUser;
        Authentication authentication = SecurityUtils.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser &&
                (loginUser = (LoginUser)authentication.getPrincipal()) != null){
            this.strictInsertFill(metaObject, "updateBy", String.class, loginUser.getUsername());
        }
    }

}
