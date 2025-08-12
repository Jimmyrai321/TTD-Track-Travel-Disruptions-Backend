package com.FlyAsh.TrackTravelDisruptions.service;

import com.FlyAsh.TrackTravelDisruptions.TrackTravelDisruptionsApplication;
import com.FlyAsh.TrackTravelDisruptions.dto.RailDataDTO;
import com.FlyAsh.TrackTravelDisruptions.exceptions.ApiKeyError;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


@Service
public class RailDataApiService {


    private final String baseUrl = "https://api1.raildata.org.uk/1010-live-fastest-departures/LDBWS/api/20220120/GetFastestDeparturesWithDetails/";
    private final WebClient webClient = WebClient.builder().baseUrl(baseUrl).defaultHeader("X-apikey", getApiKey()).build();

    private String getApiKey() {
        String key;
        try (
                var stream = TrackTravelDisruptionsApplication.class.getResourceAsStream("/apikey.txt");
                var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        ) {
            key = reader.readLine();
        } catch (IOException e) {
            throw new ApiKeyError("raildata marketplace API key not present");
        }
        return key;
    }


    public RailDataDTO getNextFastestServiceBetween(String origin, String destination, long timeOffset) {
        ResponseEntity<RailDataDTO> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment(origin + "/" + destination)
                        .queryParam("timeOffset", timeOffset)
                        .build())
                .retrieve()
                .toEntity(RailDataDTO.class)
                .block();
        assert response != null;
        return response.getBody();
    }

    public boolean checkApiHealth() {
        try {
            ResponseEntity<RailDataDTO> response = webClient.get()
                    .uri(baseUrl + "MAN/RDG")
                    .header("X-apikey", getApiKey())
                    .retrieve()
                    .toEntity(RailDataDTO.class)
                    .block();
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
}