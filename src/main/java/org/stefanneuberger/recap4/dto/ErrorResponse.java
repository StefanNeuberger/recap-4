package org.stefanneuberger.recap4.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    @Schema(example = "Resource not found")
    private String message;

    @Schema(example = "404")
    private int status;

    @Schema(example = "Not Found")
    private String error;

    private Instant timestamp;
}


