package org.example.session19_b5.controller;

import org.example.session19_b5.dto.AuthResponse;
import org.example.session19_b5.dto.LoginRequest;
import org.example.session19_b5.dto.RefreshRequest;
import org.example.session19_b5.dto.TokenResponse;
import org.example.session19_b5.entity.User;
import org.example.session19_b5.service.AuthService;
import org.example.session19_b5.service.JwtService;
import org.example.session19_b5.repository.RevokedTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RevokedTokenRepository revokedTokenRepository;








    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request){

        User user =

                authService.authenticate(

                        request.getUsername(),

                        request.getPassword()

                );

        String accessToken =

                jwtService.generateAccessToken(

                        user.getUsername()

                );

        String refreshToken =

                jwtService.generateRefreshToken(

                        user.getUsername()

                );

        return new AuthResponse(

                accessToken,

                refreshToken

        );
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(
            @RequestBody RefreshRequest request){

        String refreshToken =
                request.getRefreshToken();

        if(revokedTokenRepository
                .existsByToken(refreshToken)){

            throw new RuntimeException(
                    "Refresh token revoked"
            );
        }

        if(!jwtService.validateToken(refreshToken)){

            throw new RuntimeException(
                    "Refresh token invalid"
            );
        }

        String username =
                jwtService.extractUsername(
                        refreshToken
                );

        String accessToken =
                jwtService.generateAccessToken(
                        username
                );

        return new TokenResponse(
                accessToken
        );
    }
}
