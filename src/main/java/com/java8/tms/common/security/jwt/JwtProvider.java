package com.java8.tms.common.security.jwt;


import com.java8.tms.common.security.userprincipal.UserPrinciple;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Component
public class JwtProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    private final String jwtSecret = "h9xV5v8PSz43";
    @Value("${app.jwtExpirationMs}")
    long jwtExpiration;

    public String createToken(UserPrinciple userPrinciple) {
//        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

        return Jwts.builder().setSubject(userPrinciple.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+ jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getJwt(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer")){
            return authHeader.replace("Bearer", "");
        }
        return null;
    }

    public boolean validateToken(String token) throws SignatureException, MalformedJwtException, UnsupportedJwtException, ExpiredJwtException, IllegalArgumentException{
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        }catch (SignatureException e){
            logger.error("Invalid JWT signature: {}", e.getMessage());
        }catch (MalformedJwtException e2){
            logger.error("Invalid JWT token format: {}", e2.getMessage());
        }catch (UnsupportedJwtException e3){
            logger.error("Unsupported JWT: {}", e3.getMessage());
        }catch (ExpiredJwtException e4){
            logger.error("Expired Jwt token: {}", e4.getMessage());
        }catch (IllegalArgumentException e5){
            logger.error("Jwt claims string is empty: {}", e5.getMessage());
        }
        return false;
    }

    public void validateTokenThrowException(String token) throws Exception{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
    }

    public String getEmailFromToken(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public Date getAccessTokenExpiredTime(String token){
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getExpiration();
    }
}
