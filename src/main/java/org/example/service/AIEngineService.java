package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class AIEngineService {

    private final WebClient webClient;

    public AIEngineService(WebClient.Builder webClientBuilder, @Value("${ai.engine.url}") String aiEngineUrl) {
        this.webClient = webClientBuilder.baseUrl(aiEngineUrl).build();
    }

    public Mono<String> getFaceEmbedding(MultipartFile image) {
        return Mono.fromCallable(() -> {
            try {
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("image", new ByteArrayResource(image.getBytes()) {
                    @Override
                    public String getFilename() {
                        return image.getOriginalFilename();
                    }
                });
                return body;
            } catch (IOException e) {
                throw new RuntimeException("Failed to read image", e);
            }
        }).flatMap(body ->
                webClient.post()
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .body(BodyInserters.fromMultipartData(body))
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(error -> {
                            // Log the error
                        })
        );
    }
}