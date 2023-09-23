package com.autogpt4j.config;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface HuggingFaceApiClient {

    @RequestLine("POST /models/{model}")
    @Headers({ "Authorization: Bearer {token}", "Content-Type: application/json", "X-Use-Cache: {cacheUse}" })
    byte[] generateImageWithHuggingFace(@Param String token, @Param String model, @Param boolean cacheUse,
            String prompt);
}
