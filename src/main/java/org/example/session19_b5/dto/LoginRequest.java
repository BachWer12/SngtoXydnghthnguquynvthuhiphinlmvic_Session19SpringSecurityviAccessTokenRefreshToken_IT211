package org.example.session19_b5.dto;

import lombok.*;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class LoginRequest {

    private String username;

    private String password;
}
