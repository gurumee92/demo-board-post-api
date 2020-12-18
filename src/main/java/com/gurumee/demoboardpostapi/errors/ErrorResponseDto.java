package com.gurumee.demoboardpostapi.errors;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString @EqualsAndHashCode
@Builder
public class ErrorResponseDto {
    private String message;
}
