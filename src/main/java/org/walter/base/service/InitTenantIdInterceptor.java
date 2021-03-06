package org.walter.base.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class InitTenantIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenantId = request.getParameter("tenantId");
        if(StringUtils.isEmpty(tenantId)){
            log.error("请求中未设置租户ID");
            return false;
        }
        // 初始化用户态的租户ID
        MultiTenantContextHolder.initUserTenantId(tenantId);
        return true;
    }
}
