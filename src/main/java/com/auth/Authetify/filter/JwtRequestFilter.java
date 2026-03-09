package com.auth.Authetify.filter;

import com.auth.Authetify.service.AppUserDetailsService;
import com.auth.Authetify.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter{
    private final AppUserDetailsService appUserDetailsService;
    private  final JwtUtil jwtUtil;
    private static final List<String> PUBLIC_URLS = List.of("/login","/register","/send-reset-otp","/reset-password","/logout");
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String path = request.getServletPath();
        if(PUBLIC_URLS.contains(path)){
            chain.doFilter(request, response);
            return;
        }
        String jwt = null;
        String email = null;
        final String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader !=null && authorizationHeader.startsWith("Bearer ")){
            authorizationHeader.substring(7);
        }
         if(jwt==null){
             Cookie[] cookie = request.getCookies();
             if(cookie !=null){
                 for(Cookie cookies: cookie ){
                     if("jwt".equals(cookies.getName())){
                         jwt = cookies.getValue();
                         break;
                     }
                 }
             }
         }

         if(jwt != null){
             email = jwtUtil.extractEmail(jwt);
             if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                 UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);
                 if(jwtUtil.validateToken(jwt, userDetails)){
                   UsernamePasswordAuthenticationToken authenticationToken = new  UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                 authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                 SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                 }
             }
        }
        chain.doFilter(request, response);
    }
}
