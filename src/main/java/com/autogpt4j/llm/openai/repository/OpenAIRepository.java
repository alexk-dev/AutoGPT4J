package com.autogpt4j.llm.openai.repository;

import com.autogpt4j.config.AppProperties;
import com.autogpt4j.content.Content;
import com.autogpt4j.llm.tokenization.TokensAndChunking;
import com.autogpt4j.llm.tokenization.model.OpenAIChunker;
import com.knuddels.jtokkit.api.ModelType;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class OpenAIRepository {

    private final TokensAndChunking tokensAndChunking;
    private final OpenAiService openAiService;
    private final AppProperties appProperties;

    public OpenAIRepository(AppProperties appProperties, OpenAIChunker tokensAndChunking) {
        this.appProperties = appProperties;
        this.tokensAndChunking = tokensAndChunking;
        this.openAiService = new OpenAiService(appProperties.getOpenAiApiKey());
    }

    public Content getEmbeddings(Content content) {
        OpenAiService service = new OpenAiService(appProperties.getOpenAiApiKey());

        tokensAndChunking.chunkContent(content);

        content.getChunks().stream()
                .forEach(chunk -> {
                    EmbeddingRequest embeddingRequest = EmbeddingRequest.builder()
                            .model(tokensAndChunking.getEmbeddingModelName())
                            .input(Collections.singletonList(chunk.getSubText()))
                            .build();

                    Embedding embedding = service.createEmbeddings(embeddingRequest).getData().get(0);

                    chunk.setEmbedding(embedding);
                });

        return content;
    }

    public List<ChatCompletionChoice> getCompletion(List<ChatMessage> chatMessages) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(appProperties.getOpenAiModel())
                .messages(chatMessages)
                .build();

        var request = openAiService.createChatCompletion(chatCompletionRequest);

        return request.getChoices();
    }

    public TokensAndChunking getTokenAndChunking() {
        return tokensAndChunking;
    }
}
