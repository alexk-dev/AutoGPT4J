package com.autogpt4j.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DalleRequestDto {
    private String prompt;
    @Builder.Default
    private int n = 1;
    private String size;
}
