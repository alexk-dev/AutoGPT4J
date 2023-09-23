package com.autogpt4j.config;

import com.autogpt4j.dto.DalleRequestDto;
import com.autogpt4j.dto.SDWebUIRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface SDWebUIClient {

    @RequestLine("POST /sdapi/v1/txt2img")
    @Headers({ "Authorization: Basic {digest}", "Content-Type: application/json" })
    JsonNode generateImageWithSDWebUI(@Param String digest, SDWebUIRequestDto dto);
}
