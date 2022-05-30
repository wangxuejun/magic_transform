package com.dd.nio.common.utils;

import com.dd.nio.common.exception.ServiceException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtils {
    // 签名私钥
    public static final String AUTH_HEADER_KEY = "Authorization";
    // 签名的失效时间
    private static final long TTL = 1000000 ;
    /**
     * 设置认证token
     * id:登录用户id
     * subject:登录用户名
     */

    public static String createJwt(String id, String name, Map<String,Object> map) throws ServiceException {
        // 设置失效时间
        long now = System.currentTimeMillis(); // 当前毫秒数
        long exp = now + TTL;

        // 创建jwtBuilder
        String token = null;
        try {
            JwtBuilder jwtBuilder = Jwts.builder()
                    .setId(id) // 添加id
                    .setSubject(name) // 添加用户名
                    .setIssuedAt(new Date()) // 添加当前时间
                    .signWith(SignatureAlgorithm.HS256,AUTH_HEADER_KEY); // 设置加密算法，密钥

            // 根据map设置claims
            for (Map.Entry<String,Object> entry : map.entrySet()){
                jwtBuilder.claim(entry.getKey(),entry.getValue());
            }

            // 指定失效时间
            jwtBuilder.setExpiration(new Date(exp));

            // 创建token
            token = jwtBuilder.compact();
        } catch (Exception e) {
            log.error("签名失败", e);
            throw new ServiceException("签名失败");
        }
        return token;
    }

    /**
     * 解析token字符串，获取clamis
     */
    public static Claims parseJwt(String token) throws ServiceException {

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(AUTH_HEADER_KEY)
                    .parseClaimsJws(token).getBody();
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("===== Token过期 =====", e);
            throw new ServiceException("token 过期");
        } catch (Exception e) {
            log.error("===== token解析异常 =====", e);
            throw new ServiceException("token 解析异常");
        }
    }
}