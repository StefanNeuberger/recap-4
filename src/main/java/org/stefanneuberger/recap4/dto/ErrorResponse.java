package org.stefanneuberger.recap4.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public class ErrorResponse {

    @Schema(example = "Resource not found")
    private  String message;

    @Schema(example = "404")
    private  int status;

    @Schema(example = "Not Found")
    private  String error;

    private  Instant timestamp;

    public ErrorResponse() {
        this("", 0, "", Instant.now());
    }

    public ErrorResponse(String message, int status, String error, Instant timestamp) {
        this.message = message;
        this.status = status;
        this.error = error;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}


