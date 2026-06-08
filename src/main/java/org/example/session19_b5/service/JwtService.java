package org.example.session19_b5.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.ES256);

    private final Long ACCESS_EXPIRATION = TimeUnit.DAYS.toMillis(15);



    private final long REFRESH_EXPIRATION =

            TimeUnit.DAYS.toMillis(7);

    public String generateAccessToken(String username){

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .compact();
    }


    public String extractUsername(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;
        }catch (Exception e){
            return false;
        }
    }


}
