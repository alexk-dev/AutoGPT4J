package com.autogpt4j.command;

import com.autogpt4j.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExecuteCodeCommand implements Command {

    private final AppProperties appProperties;

    public ExecuteCodeCommand(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public String getName() {
        return "ExecuteCodeCommand";
    }

    @Override
    public String getDescription() {
        return "ExecuteCodeCommand";
    }

    public String execute(Map<String, Object> params) {
        String fileName = (String) params.get("fileName");
        return executePythonCode(fileName);
    }

    private String executePythonCode(String fileName) {
        if (!getExtension(fileName).equals("py")) {
            return "Error: Invalid file type. Only .py files are allowed.";
        } else if (!fileExists(fileName)) {
            return String.format("Error: File %s does not exist.", fileName);
        }

        return runPythonFile(fileName).stream()
                .collect(Collectors.joining(" "));

    }

    private List<String> runPythonFile(String fileName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", getFile(fileName).getAbsolutePath());
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            List<String> results = readProcessOutput(process.getInputStream());

            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> readProcessOutput(InputStream inputStream) {
        try (BufferedReader output = new BufferedReader(new InputStreamReader(inputStream))) {
            return output.lines()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File getFile(String fileName) {
        return Paths.get(appProperties.getFilesLocation(), fileName).toFile();
    }

    private boolean fileExists(String fileName) {
        return getFile(fileName).exists();
    }

    private String getExtension(String fileName) {
        return getExtensionByStringHandling(fileName)
                .orElseThrow(() -> new RuntimeException("File does not have an extension."));
    }

    private Optional<String> getExtensionByStringHandling(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1));
    }

    private boolean isDockerContainer() {
        return new File("./.dockerenv").exists();
    }
}
