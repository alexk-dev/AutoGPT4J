package com.autogpt4j.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private String embeddingModelName;
    private String contextModelName;
    private String logPath;
    private String filesLocation;
    private String imageProvider;
    private String huggingFaceImageModel;
    private String huggingFaceApiKey;
    @Builder.Default
    private String sdWebuiUrl = "http://localhost:7860";
    private String sdWebuiUsername;
    private String sdWebuiPassword;
    private String openAiApiKey;
    private String openAiModel;
    private String googleApiKey;
    private String googleCustomSearchEngineId;
    private String twitterConsumerKey;
    private String twitterConsumerSecret;
    private String twitterAccessToken;
    private String twitterTokenSecret;
}
