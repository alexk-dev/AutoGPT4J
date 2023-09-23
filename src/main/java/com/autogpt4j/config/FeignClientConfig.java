package com.autogpt4j.config;

import feign.Feign;
import feign.Logger;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {
    private static final OkHttpClient client = new OkHttpClient();

    @Bean
    public HuggingFaceApiClient huggingFaceApiClient(AppProperties appProperties) {
        return Feign.builder()
                .client(client)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .retryer(new Retryer.Default(1000, 50_000, 5))
                .logger(new Slf4jLogger())
                .logLevel(Logger.Level.BASIC)
                .target(HuggingFaceApiClient.class, "https://api-inference.huggingface.co");
    }

    @Bean
    public OpenAIApiClient openAIApiClient(AppProperties appProperties) {
        return Feign.builder()
                .client(client)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .retryer(new Retryer.Default(1000, 50_000, 5))
                .logger(new Slf4jLogger())
                .logLevel(Logger.Level.BASIC)
                .target(OpenAIApiClient.class, "https://api.openai.com");
    }

    @Bean
    public SDWebUIClient sdWebUIClient(AppProperties appProperties) {
        return Feign.builder()
                .client(client)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .retryer(new Retryer.Default(1000, 50_000, 5))
                .logger(new Slf4jLogger())
                .logLevel(Logger.Level.BASIC)
                .target(SDWebUIClient.class, appProperties.getSdWebuiUrl());
    }

}