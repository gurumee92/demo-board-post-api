package com.gurumee.demoboardpostapi.posts;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString @EqualsAndHashCode(of = "id")
@Builder
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String ownerName;

    @DateTimeFormat(pattern = "yyyy-mm-dd HH:MM:ss")
    private LocalDateTime created_at;

    @DateTimeFormat(pattern = "yyyy-mm-dd HH:MM:ss")
    private LocalDateTime updated_at;
}
