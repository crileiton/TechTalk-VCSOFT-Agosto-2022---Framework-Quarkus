package org.quarkus.vcsoft.rest.estudianteApis;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.quarkus.vcsoft.domain.InformacionDeContacto;
import org.quarkus.vcsoft.helpers.ValidationGroups;
import org.quarkus.vcsoft.helpers.exception.VCException;
import org.quarkus.vcsoft.services.EstudianteService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transaccion/estudiante/actualizar")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActualizarEstudianteApi {

    private static final Logger LOG = Logger.getLogger(ActualizarEstudianteApi.class);

    @Inject
    EstudianteService estudianteService;

    @PUT
    @Path("/{idEstudiante}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Actualizar_Info_Contacto_Estudiante")
    @Operation(
            summary = "Actualización de la información de contacto de un estudiante en mongo",
            description = "Permite la actualización de la información de contacto de un estudiante en mongo"
    )
    public Response actualizarInfoContactoEstudiante(
            @Parameter(
                    name = "idEstudiante",
                    description = "Identificador del estudiante",
                    example = "8c030024-f77b-48b4-91ef-dd3bc5fbe438",
                    schema = @Schema(type = SchemaType.STRING, format = "uuid")
            )
            @NotEmpty
            @PathParam("idEstudiante") String idEstudiante,
            @RequestBody(
                    description = "Contenido para la actualización de la información de contacto del estudiante",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation =
                            InformacionDeContacto.class))
            )
            @Valid @ConvertGroup(to = ValidationGroups.Put.class) InformacionDeContacto informacionDeContacto)
            throws VCException {

        LOG.debugf("@actualizarInfoContactoEstudiante API> Inicia ejecución del servicio");

        estudianteService.actualizarInformacionContactoEstudiante(informacionDeContacto, idEstudiante);

        LOG.debugf("@actualizarInfoContactoEstudiante API> Finaliza ejecución del servicio");

        return Response.status(Response.Status.CREATED).build();
    }

}
