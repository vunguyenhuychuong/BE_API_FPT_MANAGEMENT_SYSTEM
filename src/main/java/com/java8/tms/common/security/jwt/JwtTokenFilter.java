package com.java8.tms.common.security.jwt;

import com.java8.tms.common.repository.BlackAccessTokenRepository;
import com.java8.tms.common.security.userprincipal.CustomUserDetailService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//Gọi lại lớp JwtProvider để validate
//OncePerRequestFilter tìm token trong request
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserDetailService userDetailService;

    @Autowired
    private BlackAccessTokenServiceImp blackAccessTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtProvider.getJwt(request);
            if (token != null && jwtProvider.validateToken(token) && null == blackAccessTokenService.findByAccessToken(DigestUtils.sha3_256Hex(token))){
                String email = jwtProvider.getEmailFromToken(token);
                UserDetails userDetails = userDetailService.loadUserByUsername(email);
                if(userDetails.isEnabled() && userDetails.isAccountNonExpired() && userDetails.isAccountNonLocked()) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }catch (Exception e){
            logger.error("Can't set user authentication -> Message: ", e);
        }
        filterChain.doFilter(request, response);
    }

}
