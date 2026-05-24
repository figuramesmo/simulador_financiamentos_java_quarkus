package org.caixa.financiamentos.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.caixa.financiamentos.dto.FinanciamentoRequestDTO;

@Path("/financimentos")
public class SimulacaoResource {


    @POST
    public Response criaFinanciamento(
        FinanciamentoRequestDTO requestDTO
    ){

        return Response
                .created()
                .status(Response.Status.CREATED)
                .entity()
                .build();
    }

}
