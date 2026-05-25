package org.caixa.financiamentos.errorhandler;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String mensagem;
    private int status;
    @JsonProperty("quando")
    private LocalDateTime currentDateTime;

    public ErrorResponse(String mensagem, int status, LocalDateTime currentDateTime) {
        this.mensagem = mensagem;
        this.status = status;
        this.currentDateTime = currentDateTime;
    }

    public String getMensagem() {
        return mensagem;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getCurrentDateTime() {
        return currentDateTime;
    }
}
