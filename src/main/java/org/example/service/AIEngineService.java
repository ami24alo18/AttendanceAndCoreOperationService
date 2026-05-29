package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Service
public class AIEngineService {

    private final WebClient webClient;

    public AIEngineService(WebClient.Builder webClientBuilder, @Value("${ai.engine.url}") String aiEngineUrl) {
        this.webClient = webClientBuilder.baseUrl(aiEngineUrl).build();
    }

    public Mono<String> getFaceEmbedding(MultipartFile image) {
        return webClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("image", image.getResource()))
                .retrieve()
                .bodyToMono(String.class);
    }
}