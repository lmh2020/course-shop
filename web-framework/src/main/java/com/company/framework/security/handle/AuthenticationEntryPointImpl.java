package com.company.framework.security.handle;

import java.io.IOException;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.company.common.enums.HttpStatusEnum;
import com.jhlabs.math.FFT;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.company.common.core.domain.R;
import com.company.common.utils.ServletUtils;
import com.company.common.utils.StringUtils;

/**
 * 认证失败处理类 返回未授权
 *
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    private final static String frontUrlPrefix = "/front";


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        String url = request.getRequestURI();
        if (url.startsWith(frontUrlPrefix)){
            try {
                response.sendRedirect(frontUrlPrefix + "/loginPage");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {
            String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
            ServletUtils.renderString(response, JSON.toJSONString(R.fail(HttpStatusEnum.UNAUTHORIZED, msg)));
        }
    }

}
