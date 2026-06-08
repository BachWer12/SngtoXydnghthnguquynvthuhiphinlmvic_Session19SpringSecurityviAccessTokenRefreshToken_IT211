package org.example.session19_b5.dto;

import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor

public class RefreshRequest {
    private String refreshToken;
}
