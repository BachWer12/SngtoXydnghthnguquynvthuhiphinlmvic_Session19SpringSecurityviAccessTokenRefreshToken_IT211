package org.example.session19_b5.dto;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}
