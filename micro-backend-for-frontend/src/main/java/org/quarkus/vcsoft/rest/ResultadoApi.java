package org.quarkus.vcsoft.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.quarkus.vcsoft.domain.Resultado;
import org.quarkus.vcsoft.helpers.ValidationGroups;
import org.quarkus.vcsoft.helpers.exception.Problem;
import org.quarkus.vcsoft.helpers.exception.VCException;
import org.quarkus.vcsoft.services.ResultadoService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/bff/resultado")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResultadoApi {

    private static final Logger LOG = Logger.getLogger(ResultadoApi.class);

    @Inject
    ResultadoService resultadoService;

    @PUT
    @Path("/guardar")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Guardar_Resultado")
    @Operation(
            summary = "Guardar resultado",
            description = "Permite el guardado de un resultado en mongo"
    )
    public Response guardarResultadoEnMongo(
            @RequestBody(
                    description = "Contenido para el guardado del resultado en mongo",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Resultado.class))
            )
            @Valid @ConvertGroup(to = ValidationGroups.Put.class) Resultado resultado) {

        LOG.debugf("@guardarResultadoEnMongo API> Inicia ejecución del servicio");

        resultadoService.guardarResultadoEstudiante(resultado);

        LOG.debugf("@guardarResultadoEnMongo API> Finaliza ejecución del servicio");

        return Response.status(Response.Status.CREATED).build();
    }

}
