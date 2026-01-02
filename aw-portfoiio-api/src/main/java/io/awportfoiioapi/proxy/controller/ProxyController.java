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
        // http만 허용
        // http만 허용 (필요하면 https도 추가 가능)
        if (!url.startsWith("http://")) {
            return ResponseEntity.badRequest().build();
        }
        RestClient restClient = RestClient.create();
        // 원본 응답을 상태/헤더/바디 통째로 받음
        
        ResponseEntity<byte[]> upstream = restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(byte[].class);

        HttpHeaders headers = new HttpHeaders();
        headers.putAll(upstream.getHeaders());

        // iframe 차단 헤더 제거
        headers.remove("X-Frame-Options");
        headers.remove("Content-Security-Policy");


        // 그대로 반환
        return ResponseEntity
                .status(upstream.getStatusCode())
                .headers(headers)
                .body(upstream.getBody());
    }
}