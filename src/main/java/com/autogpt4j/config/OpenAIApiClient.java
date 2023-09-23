package com.autogpt4j.config;

import com.autogpt4j.dto.DalleRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface OpenAIApiClient {

    @RequestLine("POST /v1/images/generations")
    @Headers({ "Authorization: Bearer {token}", "Content-Type: application/json" })
    JsonNode generateImageWithDalle(@Param String token, DalleRequestDto dto);
}
