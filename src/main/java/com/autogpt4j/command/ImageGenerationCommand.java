package com.autogpt4j.command;

import com.autogpt4j.config.AppProperties;
import com.autogpt4j.config.HuggingFaceApiClient;
import com.autogpt4j.config.OpenAIApiClient;
import com.autogpt4j.config.SDWebUIClient;
import com.autogpt4j.dto.DalleRequestDto;
import com.autogpt4j.dto.SDWebUIRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Component
public class ImageGenerationCommand implements Command {

    private final String fileName;
    private final AppProperties appProperties;
    private final HuggingFaceApiClient huggingFaceApiClient;
    private final OpenAIApiClient openAIApiClient;
    private final SDWebUIClient sdWebUIClient;

    public ImageGenerationCommand(AppProperties appProperties, HuggingFaceApiClient huggingFaceApiClient,
            OpenAIApiClient openAIApiClient, SDWebUIClient sdWebUIClient) {
        this.appProperties = appProperties;
        this.huggingFaceApiClient = huggingFaceApiClient;
        this.openAIApiClient = openAIApiClient;
        this.sdWebUIClient = sdWebUIClient;

        this.fileName = Paths.get(appProperties.getFilesLocation(), UUID.randomUUID() + ".jpg").toString();
    }

    @Override
    public String getName() {
        return "ImageGenerationCommand";
    }

    @Override
    public String getDescription() {
        return "ImageGenerationCommand";
    }

    public String execute(Map<String, Object> params) {
        String prompt = (String) params.get("prompt");
        Integer size = (Integer) params.get("size");
        return generateImage(prompt, size);
    }

    public String generateImage(String prompt, Integer size) {
        try {
            String imageProvider = appProperties.getImageProvider().toLowerCase();
            switch (imageProvider) {
            case "dalle":
                return generateImageWithDalle(prompt, size);
            case "huggingface":
                return generateImageWithHuggingFace(prompt, size);
            case "sdwebui":
                return generateImageWithSDWebUI(prompt, size);
            default:
                throw new RuntimeException("No Image Provider Set");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String generateImageWithHuggingFace(String prompt, Integer size) {
        if (appProperties.getHuggingFaceApiKey() == null) {
            throw new IllegalArgumentException("You need to set your Hugging Face API token in the config file.");
        }

        try {
            byte[] imageBytes = huggingFaceApiClient.generateImageWithHuggingFace(appProperties.getHuggingFaceApiKey(),
                    appProperties.getHuggingFaceImageModel(), false, prompt);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            ImageIO.write(image, "jpg", new File(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate image", e);
        }
    }

    private String generateImageWithDalle(String prompt, Integer size) {
        if (appProperties.getOpenAiApiKey() == null) {
            throw new IllegalArgumentException("You need to set your OpenAI API key in the config file.");
        }

        try {
            JsonNode response = openAIApiClient.generateImageWithDalle(appProperties.getOpenAiApiKey(),
                    DalleRequestDto.builder()
                            .prompt(prompt)
                            .size(size + "x" + size)
                            .build());

            ArrayNode arrayNode = (ArrayNode) response.get("images");
            String base64Image = arrayNode.get(0).asText();
            byte[] imageBytes = Base64.decodeBase64(base64Image.split(",", 2)[1]);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            ImageIO.write(image, "jpg", new File(fileName));
            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate image", e);
        }
    }

    private String generateImageWithSDWebUI(String prompt, Integer size) {
        if (appProperties.getSdWebuiUrl() == null || appProperties.getSdWebuiUsername() == null
                || appProperties.getSdWebuiPassword() == null) {
            throw new IllegalArgumentException(
                    "You need to set your SDWebUI url, username and password in the config file.");
        }

        try {
            String auth = appProperties.getSdWebuiUsername() + ":" + appProperties.getSdWebuiPassword();
            String digest = Base64.encodeBase64String(auth.getBytes(StandardCharsets.ISO_8859_1));

            JsonNode response = sdWebUIClient.generateImageWithSDWebUI(digest, SDWebUIRequestDto.builder()
                    .prompt(prompt)
                    .width(size)
                    .height(size)
                    .build());

            ArrayNode arrayNode = (ArrayNode) response.get("images");
            String base64Image = arrayNode.get(0).asText();
            byte[] imageBytes = Base64.decodeBase64(base64Image.split(",", 2)[1]);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            ImageIO.write(image, "jpg", new File(fileName));
            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate image", e);
        }
    }

}
