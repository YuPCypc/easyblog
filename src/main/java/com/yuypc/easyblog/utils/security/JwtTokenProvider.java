package com.yuypc.easyblog.utils.security;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    @Value("${easyblog.jwt.secret}")
    private String secretKey;

    @Value("${easyblog.jwt.expiration}")
    private long validityInMilliseconds;

    @Value("${easyblog.jwt.tokenHead}")
    private String tokenHead;

    private static final String CLAIM_KEY_CREATED = "created";

    public String createToken(String username,String avatar) {
        Claims claims = Jwts.claims();
        claims.put("username", username); // 存储用户名
        claims.put("avatar", avatar); // 存储角色
        Date now = new Date();
        claims.put(CLAIM_KEY_CREATED, now); // 存储创建时间
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return generateToken(claims, validity);
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            throw new RuntimeException("解析token失败");
        }
    }

    public String getUsername(String token) {
        return getClaims(token).get("username", String.class);
    }

    public String getAvatar(String token) {
        return getClaims(token).get("avatar", String.class);
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public String refreshHeadToken(String oldToken) {
        Claims claims = getClaims(oldToken);
        if (tokenRefreshJustBefore(oldToken, 30 * 60)) {
            return oldToken;
        } else {
            claims.put(CLAIM_KEY_CREATED, new Date());
            return generateToken(claims, new Date(new Date().getTime() + validityInMilliseconds));
        }
    }

    public String generateToken(Claims claims, Date expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private boolean tokenRefreshJustBefore(String token, int time) {
        Claims claims = getClaims(token);
        Date created = claims.get(CLAIM_KEY_CREATED, Date.class);
        Date refreshDate = new Date();
        return refreshDate.after(created) && refreshDate.before(DateUtil.offsetSecond(created, time));
    }

    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }

    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration();
    }
}
