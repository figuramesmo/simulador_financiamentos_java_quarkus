package org.caixa.financiamentos.errorhandler;

import io.quarkus.logging.Log;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.LocalDateTime;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        LocalDateTime currentTime = LocalDateTime.now();
        ErrorResponse errorResponse = new ErrorResponse(
                exception.getMessage(),
                Response.Status.BAD_REQUEST.getStatusCode(),
                currentTime
        );

        Log.error("Entrada invalida: {}", exception.getMessage(), exception);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

