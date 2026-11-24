package co.edu.unbosque.backcaoba.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    /** Minutos de vigencia del token. */
    @Value("${jwt.expiration-minutes}")
    private long expiracion;

    public String generarToken(String correo, String rol) {
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(new Date(ahora))
                .expiration(new Date(ahora + expiracion * 60_000L))
                .signWith(obtenerClave())
                .compact();
    }

    public String extraerCorreo(String token) {
        return extraerClaims(token).getSubject();
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        Claims claims = extraerClaims(token);
        return claims.getSubject().equals(userDetails.getUsername())
                && claims.getExpiration().after(new Date());
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser().verifyWith(obtenerClave()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
