package com.autogpt4j.command;

import com.autogpt4j.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@Component
public class FileCommand implements Command {

    public final static String WRITE = "write";
    public final static String APPEND = "append";
    public final static String DELETE = "delete";
    public final static String READ = "read";

    private final AppProperties appProperties;
    private final ObjectMapper mapper;
    private final CommandLog commandLog;

    public FileCommand(AppProperties appProperties, ObjectMapper mapper, CommandLog commandLog) {
        this.appProperties = appProperties;
        this.mapper = mapper;
        this.commandLog = commandLog;
    }

    @Override
    public String getName() {
        return "FileCommand";
    }

    @Override
    public String getDescription() {
        return "FileCommand";
    }

    public String execute(Map<String, Object> params) {
        String commandName = (String) params.get("commandName");
        String fileName = (String) params.get("fileName");
        String text = (String) params.get("text");
        String output = "";

        switch (commandName) {
        case WRITE:
            writeFile(fileName, text);
            break;
        case APPEND:
            appendToFile(fileName, text);
            break;
        case DELETE:
            deleteFile(fileName);
            break;
        case READ:
            output = readFile(fileName);
            break;
        }

        return output;
    }

    private void deleteFile(String fileName) {
        log.warn("DELETING FILE: {}", getFile(fileName).getAbsolutePath());

        try {
            FileUtils.delete(getFile(fileName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void appendToFile(String fileName, String text) {
        log.warn("APPENDING TO FILE: {}", getFile(fileName).getAbsolutePath());

        try {
            FileUtils.writeStringToFile(getFile(fileName), text, Charset.forName("UTF-8"), true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeFile(String fileName, String text) {
        log.warn("WRITING FILE: {}", getFile(fileName).getAbsolutePath());

        try {
            FileUtils.writeStringToFile(getFile(fileName), text, Charset.forName("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String readFile(String fileName) {
        log.warn("READING FILE: {}", getFile(fileName).getAbsolutePath());

        try {
            return FileUtils.readFileToString(getFile(fileName));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File getFile(String fileName) {
        return Paths.get(appProperties.getFilesLocation(), fileName).toFile();
    }
}
