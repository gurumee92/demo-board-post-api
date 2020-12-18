package com.gurumee.demoboardpostapi.posts;

import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString @EqualsAndHashCode
@Builder
public class UpdatePostRequestDto {
    @NotNull
    @NotEmpty
    @JsonProperty(value = "title")
    private String title;

    @NotNull @NotEmpty
    @JsonProperty(value = "content")
    private String content;
}
