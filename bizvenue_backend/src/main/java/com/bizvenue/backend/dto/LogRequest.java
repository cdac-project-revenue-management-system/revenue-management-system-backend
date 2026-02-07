package com.bizvenue.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogRequest {
    private String serviceName;
    private String environment;
    private String logLevel;
    private String message;
    private String exceptionDetails;
    private String traceId;
    private String clientIp;
    private String createdBy;
}
