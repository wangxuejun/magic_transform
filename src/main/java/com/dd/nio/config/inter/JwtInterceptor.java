package com.dd.nio.config.inter;

import com.dd.nio.common.api.JwtIgnore;
import com.dd.nio.common.exception.ServiceException;
import com.dd.nio.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtInterceptor extends HandlerInterceptorAdapter {

    /**
     * 通过拦截器获取token数据
     * 从token中解析获取claims
     * 将claims绑定到request域中
     */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 忽略带JwtIgnore注解的请求, 不做后续token认证校验
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            JwtIgnore jwtIgnore = handlerMethod.getMethodAnnotation(JwtIgnore.class);
            if (jwtIgnore != null) {
                return true;
            }
        }

        // 通过request获取请求token信息
        String authorization = request.getHeader("Authorization");
        // 判断请求头信息是否为空，或是否以Bearer 开头
        if (!StringUtils.isEmpty(authorization) && authorization.startsWith("Bearer")) {
            // 获取token数据
            String token = authorization.replace("Bearer", "");
            // 解析token获取claims
            Claims claims = JwtUtils.parseJwt(token);
            if (claims != null) {
                // 通过claims获取到当前用户的可访问api权限字符串
                String apis = (String) claims.get("roles");
                // 通过handler
                String name = null;
                if (handler instanceof HandlerMethod) {
                    HandlerMethod h = (HandlerMethod) handler;
                    // 获取接口上的requestmapping注解
                    RequestMapping annotation = h.getMethodAnnotation(RequestMapping.class);
                    // 获取当前请求接口中的name属性
                    name = annotation.name();
                } else {
                    throw new ServiceException("认证失败");
                }
                if (apis.contains(name)) {
                    request.setAttribute("userClaims", claims);
                    return true;
                } else {
                    throw new ServiceException("认证失败");
                }
            }
        }
        throw new ServiceException("用户未登陆");
    }
}