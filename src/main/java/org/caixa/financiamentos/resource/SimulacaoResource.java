package org.caixa.financiamentos.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.caixa.financiamentos.dto.FinanciamentoRequestDTO;
import org.caixa.financiamentos.dto.FinanciamentoResponseDTO;
import org.caixa.financiamentos.service.SimulacaoService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.net.URI;

@Path("/financiamentos")
public class SimulacaoResource {

    private final SimulacaoService simulacaoService;

    public SimulacaoResource(SimulacaoService simulacaoService) {
        this.simulacaoService = simulacaoService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cria a simulação de um financiamento", description = "Recebe os dados necessários para criar uma simulação de financiamento e retorna os detalhes da simulação criada.")
    @APIResponse(
        responseCode = "201",
        description = "Simulação de financiamento criada com sucesso",
        content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(implementation = FinanciamentoResponseDTO.class)
        )
    )
    public Response criaFinanciamento(
        @Valid
        @NotNull(message = "O Corpo do seu request não pode ser nulo")
        FinanciamentoRequestDTO requestDTO
    ){
        FinanciamentoResponseDTO response = simulacaoService.criaFinanciamento(requestDTO);
        URI location = URI.create("/financiamentos/" + response.id());

        return Response
                .created(location)
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

}
