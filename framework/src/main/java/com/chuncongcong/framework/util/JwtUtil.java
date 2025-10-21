package com.chuncongcong.framework.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;

@Slf4j
public class JwtUtil {

    private static final String SECRET = "3q2+7w==Q6xYV9iD7R1W0lG5Oq1Kf3Qz6uT4p8=";
    private static final long EXPIRATION = 1000L * 60 * 60 * 24 * 30; // 30天

    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Long parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.valueOf(claims.getSubject());
        } catch (JwtException e) {
            log.error("解析token失败: {}", e.getMessage(), e);
            return null;
        }
    }
}

