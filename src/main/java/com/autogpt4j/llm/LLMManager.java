package com.autogpt4j.llm;

import com.autogpt4j.content.Content;
import com.autogpt4j.llm.tokenization.TokensAndChunking;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.List;

public interface LLMManager {

    Content populateEmbeddings(Content content);

    List<ChatCompletionChoice> getCompletion(List<ChatMessage> chatMessages);

    TokensAndChunking getTokenAndChunking();
}
