package com.autogpt4j.llm.tokenization.model;

import com.autogpt4j.config.AppProperties;
import com.autogpt4j.content.Chunk;
import com.autogpt4j.content.Content;
import com.autogpt4j.llm.tokenization.TokensAndChunking;
import com.google.common.collect.Lists;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OpenAIChunker extends TokensAndChunking {

    private final ModelType embeddingModelType;
    private final ModelType contextModelType;
    private final Encoding embeddingEncoding;
    private final Encoding contextEncoding;

    private final AppProperties appProperties;

    public OpenAIChunker(AppProperties appProperties) {
        this.appProperties = appProperties;
        this.embeddingModelType = ModelType.valueOf(appProperties.getEmbeddingModelName());
        this.contextModelType = ModelType.valueOf(appProperties.getContextModelName());

        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();

        this.embeddingEncoding = registry.getEncodingForModel(embeddingModelType);
        this.contextEncoding = registry.getEncodingForModel(contextModelType);
    }

    @Override
    public void chunkContent(Content content) {
        List<Integer> encodedDocument = embeddingEncoding.encode(content.getText());

        if (encodedDocument.size() > embeddingModelType.getMaxContextLength()) {
            List<List<Integer>> encodedChunks = Lists.partition(encodedDocument,
                    splitFactor(encodedDocument, embeddingModelType));
            AtomicInteger atomicInteger = new AtomicInteger(0);

            encodedChunks.stream()
                    .forEach(it -> {
                        Chunk chunk = new Chunk()
                                .setSubContentId(
                                        content.getContentId() + "-" + atomicInteger.getAndIncrement())
                                .setSubText(embeddingEncoding.decode(it));

                        content.getChunks().add(chunk);
                    });
        }
    }

    @Override
    public Integer getEmbeddingTokenCount(String content) {
        return embeddingEncoding.encode(content).size();
    }

    @Override
    public Integer getContextTokenCount(String content) {
        return contextEncoding.encode(content).size();
    }

    @Override
    public Integer getMaximumContextTokens() {
        return contextModelType.getMaxContextLength();
    }

    @Override
    public String getEmbeddingModelName() {
        return appProperties.getEmbeddingModelName();
    }

    public int splitFactor(List<Integer> encodings, ModelType modelType) {
        int size = encodings.size();

        return Math.round(((float) size) / ((float) modelType.getMaxContextLength()));
    }

}
