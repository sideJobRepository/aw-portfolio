package io.awportfoiioapi.proxy.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class ProxyController {
    
    
    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxy(@RequestParam String url) {
        if (!url.startsWith("http://")) {
            return ResponseEntity.badRequest().build();
        }
        RestClient restClient = RestClient.create();
        
        ResponseEntity<byte[]> upstream = restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(byte[].class);
        
        return ResponseEntity
                .status(upstream.getStatusCode())
                .headers(upstream.getHeaders())
                .body(upstream.getBody());
    }
}