package org.quarkus.vcsoft.rest.estudianteApis;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.quarkus.vcsoft.domain.Estudiante;
import org.quarkus.vcsoft.helpers.exception.VCException;
import org.quarkus.vcsoft.services.EstudianteService;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/bff/estudiante/obtener")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ObtenerEstudianteApi {

    private static final Logger LOG = Logger.getLogger(ObtenerEstudianteApi.class);

    @Inject
    EstudianteService estudianteService;

    @GET
    @Path("/{idEstudiante}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Obtener_Estudiante_Por_Id")
    @Operation(
            summary = "Consultar un estudiante",
            description = "Se busca un estudiante con el id ingresado"
    )
    public Response obtenerEstudiantePorId(
            @Parameter(
                    description = "Identificación UUID del estudiante",
                    example = "ee3c31a7-2a40-4e48-98df-50d1954bf6b4",
                    schema = @Schema(type = SchemaType.STRING, format = "uuid")
            )
            @NotEmpty(message = "Debe ingresar el identificador del estudiante")
            @PathParam("idEstudiante") String idEstudiante) {

        LOG.debugf("@obtenerEstudiantePorId API> Inicia ejecución del servicio");

        Estudiante estudiante = estudianteService.obtenerEstudianteMongo(idEstudiante);

        LOG.debugf("@obtenerEstudiantePorId API> Finaliza ejecución del servicio");

        return Response.status(Response.Status.OK).entity(estudiante).build();
    }

    @GET
    @Path("/{idEstudiante}/circuit")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Obtener_Estudiante_Por_Id_Circuit")
    @Operation(
            summary = "Consultar un estudiante",
            description = "Se busca un estudiante con el id ingresado"
    )
    public Response obtenerEstudiantePorIdCircuit(
            @Parameter(
                    description = "Identificación UUID del estudiante",
                    example = "ee3c31a7-2a40-4e48-98df-50d1954bf6b4",
                    schema = @Schema(type = SchemaType.STRING, format = "uuid")
            )
            @NotEmpty(message = "Debe ingresar el identificador del estudiante")
            @PathParam("idEstudiante") String idEstudiante) throws VCException {

        LOG.debugf("@obtenerEstudiantePorIdCircuit API> Inicia ejecución del servicio");

        Estudiante estudiante = estudianteService.obtenerEstudianteMongoCircuit(idEstudiante);

        LOG.debugf("@obtenerEstudiantePorIdCircuit API> Finaliza ejecución del servicio");

        return Response.status(Response.Status.OK).entity(estudiante).build();
    }

}
