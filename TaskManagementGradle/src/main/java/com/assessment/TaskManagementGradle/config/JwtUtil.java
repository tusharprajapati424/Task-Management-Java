package com.assessment.TaskManagementGradle.config;

import com.assessment.TaskManagementGradle.entity.User;
import com.assessment.TaskManagementGradle.utility.JwtValidationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY ;

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME;

    //Generate JWT Token with role
    public String generateToken(Long id, String email, String username, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Extract Email from Token
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract Role from Token
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Extract User id from Token
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    // Validate Token
    public boolean validateToken(String token) {
        // Extract the JWT token (Remove "Bearer " prefix)
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true; // Valid token
        } catch (ExpiredJwtException e) {
            throw new JwtValidationException("Token expired", HttpStatus.UNAUTHORIZED);
        } catch (SignatureException e) {
            throw new JwtValidationException("Invalid token signature", HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            throw new JwtValidationException("Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }

    // Helper method to extract claims
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parse JWT and get all claims
    private Claims extractAllClaims(String token) {
        System.out.println("extractAllClaims token :: "+token);

        // Remove "Bearer " if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}

