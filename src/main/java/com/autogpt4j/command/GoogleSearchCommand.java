package com.autogpt4j.command;

import com.autogpt4j.config.AppProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.v1.Customsearch;
import com.google.api.services.customsearch.v1.model.Result;
import com.google.api.services.customsearch.v1.model.Search;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GoogleSearchCommand implements Command {

    private final AppProperties appProperties;

    public GoogleSearchCommand(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public String getName() {
        return "GoogleSearchCommand";
    }

    @Override
    public String getDescription() {
        return "GoogleSearchCommand";
    }

    @Override
    public String execute(Map<String, Object> params) {
        String query = (String) params.get("query");
        Integer numResults = (Integer) params.get("numResults");
        return googleOfficialSearch(query, numResults);
    }

    public String googleSearch() {
        return "DuckDuckGo search is not implemented at this time.";
    }

    public String googleOfficialSearch(String query, Integer numResults) {
        List<String> searchResultsLinks = new ArrayList<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory jsonFactory = new JacksonFactory();

            HttpRequestInitializer httpRequestInitializer = request -> {
                request.setConnectTimeout(60000);
                request.setReadTimeout(60000);
            };

            Customsearch customsearch = new Customsearch.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    jsonFactory, httpRequestInitializer)
                    .setApplicationName("GoogleSearch")
                    .build();

            Customsearch.Cse.List request = customsearch.cse().list();
            request.setQ(query);
            request.setKey(appProperties.getGoogleApiKey());
            request.setCx(appProperties.getGoogleCustomSearchEngineId());
            request.setNum(numResults);

            Search results = request.execute();
            if (results.getItems() != null) {
                searchResultsLinks = results.getItems().stream().map(Result::getLink).collect(Collectors.toList());
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return safeGoogleResults(searchResultsLinks).stream()
                .collect(Collectors.joining("\n"));
    }

    private List<String> safeGoogleResults(List<String> results) {
        JSONArray jsonArray = new JSONArray(results);
        List<String> safeResults = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            safeResults.add(jsonObject.toString());
        }

        return safeResults;
    }
}