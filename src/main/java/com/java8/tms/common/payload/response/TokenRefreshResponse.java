package com.java8.tms.common.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;
}
