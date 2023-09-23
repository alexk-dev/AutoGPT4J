package com.autogpt4j.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SDWebUIRequestDto {
    private String prompt;
    @JsonProperty("sampler_index")
    @Builder.Default
    private String samplerIndex = "DDIM";
    @Builder.Default
    private Integer steps = 20;
    @JsonProperty("cfg_scale")
    @Builder.Default
    private String cfgScale = "7.0";
    private Integer width;
    private Integer height;
    @JsonProperty("n_iter")
    private String n;
}
