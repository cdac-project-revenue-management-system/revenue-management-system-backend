package com.bizvenue.backend.service;

import com.bizvenue.backend.dto.LogRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteLoggerService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${remote.logger.url}")
    private String loggerServiceUrl;

    @Value("${spring.application.name}")
    private String appName;

    @Async
    public void log(String level, String message, String exception, String traceId, String clientIp, String user) {
        try {
            LogRequest payload = LogRequest.builder()
                    .serviceName(appName)
                    .environment("DEV")
                    .logLevel(level)
                    .message(message)
                    .exceptionDetails(exception)
                    .traceId(traceId)
                    .clientIp(clientIp)
                    .createdBy(user)
                    .build();

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

            org.springframework.http.HttpEntity<LogRequest> request = new org.springframework.http.HttpEntity<>(payload,
                    headers);

            restTemplate.postForEntity(loggerServiceUrl, request, Void.class);

        } catch (Exception e) {
            System.err.println("FAILED TO SEND LOG TO REMOTE SERVICE: " + e.getMessage());
        }
    }

    public void info(String message, String user) {
        log("INFO", message, null, null, null, user);
    }

    public void warn(String message, String user) {
        log("WARN", message, null, null, null, user);
    }

    public void error(String message, String exception, String user) {
        log("ERROR", message, exception, null, null, user);
    }
}
