package com.autogpt4j.command;

import com.autogpt4j.config.AppProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CommandLog {

    private final AppProperties appProperties;
    private final ObjectMapper mapper;

    public CommandLog(AppProperties appProperties, ObjectMapper mapper) {
        this.appProperties = appProperties;
        this.mapper = mapper;
    }

    public void attemptToExecute(String commandName, Command command, Map<String, Object> params)
            throws JsonProcessingException {
        if (!isDuplicate(commandName, command)) {
            command.execute(params);
            writeToLog(commandName, command);
        }
    }

    private boolean isDuplicate(String commandName, Command command) throws JsonProcessingException {
        HashMap<String, List<Command>> commandMap = getCommandMap();

        if (commandMap.containsKey(commandName)) {
            List<Command> commands = commandMap.get(commandName);
            return commands.stream()
                    .anyMatch(it -> command.getDescription().equals(it.getDescription())
                            && command.getName().equals(it.getName()));
        }

        return false;
    }

    private void writeToLog(String commandName, Command command) throws JsonProcessingException {
        HashMap<String, List<Command>> commandMap = getCommandMap();

        if (commandMap.containsKey(commandName)) {
            commandMap.get(commandName).add(command);
        } else {
            commandMap.put(commandName, Lists.newArrayList(command));
        }

        updateLog(commandMap);
    }

    private HashMap<String, List<Command>> getCommandMap() throws JsonProcessingException {
        return mapper.readValue(readLogFile(), HashMap.class);
    }

    private void updateLog(HashMap<String, List<Command>> commandMap) {
        try {
            FileUtils.writeStringToFile(new File(appProperties.getLogPath()), mapper.writeValueAsString(commandMap),
                    StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private String readLogFile() {
        try {
            return FileUtils.readFileToString(new File(appProperties.getLogPath()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
