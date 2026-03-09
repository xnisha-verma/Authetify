package com.auth.Authetify.util;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;
    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    private String createToken(Map<String, Object> claims, String email) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
    public <T>  T extractClami(String token, Function<Claims, T> clamisResolver){
        final Claims claims = extractAllClaims(token);
        return  clamisResolver.apply(claims);
    }
    public String extractEmail(String token){
        return extractClami(token, Claims::getSubject);
    }
    public Date extractExpiration(String token){
        return extractClami(token, Claims::getExpiration);
    }
    private  Boolean isTokenExpire(String token){
        return extractExpiration(token).before(new Date());
    }
    public Boolean validateToken(String token, UserDetails userDetails){
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())&& !isTokenExpire(token));
    }
//    private String createToken(Map<String, Object> claims, String email) {
//       return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(email)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*10))
//                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
//                .compact();
//
//    }
}
