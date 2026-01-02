package io.awportfoiioapi.proxy.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/api")
@RequiredArgsConstructor
public class ProxyController {
    
    private final RestClient restClient;

    @GetMapping("/proxy")
    public ResponseEntity<String> proxy(@RequestParam String url) {
        // http만 허용
        if (!url.startsWith("http://")) {
            return ResponseEntity.badRequest().body("Only http allowed");
        }

        String body = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);

        return ResponseEntity.ok(body);
    }
}