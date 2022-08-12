package org.quarkus.vcsoft.rest.estudianteApis;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.quarkus.vcsoft.domain.Estudiante;
import org.quarkus.vcsoft.helpers.RespuestaGuardadoMongo;
import org.quarkus.vcsoft.helpers.ValidationGroups;
import org.quarkus.vcsoft.helpers.exception.VCException;
import org.quarkus.vcsoft.services.EstudianteService;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/transaccion/estudiante/guardar")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuardarEstudianteApi {

    private static final Logger LOG = Logger.getLogger(GuardarEstudianteApi.class);

    @Inject
    Validator validator;

    @Inject
    EstudianteService estudianteService;

    @POST
    @Path("/validator-endpoint")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Guardar_Estudiante_Validación_Endpoint")
    @Operation(
            summary = "Guardar estudiante",
            description = "Permite el guardado de un estudiante en mongo realizando comprobación de validators " +
                    "en el endpoint"
    )
    public Response guardarEstudianteEnMongoEndpointValidation(
            @RequestBody(
                    description = "Contenido para el guardado del estudiante en mongo",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Estudiante.class))
            )
            @Valid @ConvertGroup(to = ValidationGroups.Post.class) Estudiante estudiante) {

        LOG.debugf("@guardarEstudianteEnMongoEndpointValidation API> Inicia ejecución del servicio");

        RespuestaGuardadoMongo respuesta = estudianteService.guardarEstudianteMongo(estudiante);

        LOG.debugf("@guardarEstudianteEnMongoEndpointValidation API> Finaliza ejecución del servicio");

        return Response.status(Response.Status.CREATED).entity(respuesta).build();
    }

    @POST
    @Path("/validator-manual")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Guardar_Estudiante_Validación_Manual")
    @Operation(
            summary = "Guardar estudiante",
            description = "Permite el guardado de un estudiante en mongo realizando comprobación de validators " +
                    "manualmente"
    )
    public Response guardarEstudianteEnMongoValidatorManual(Estudiante estudiante) throws VCException {

        LOG.debugf("@guardarEstudianteEnMongoValidatorManual API> Inicia ejecución del servicio");

        Set<ConstraintViolation<Estudiante>> violations = validator.validate(estudiante);

        if (violations.isEmpty()) {
            RespuestaGuardadoMongo respuesta = estudianteService.guardarEstudianteMongo(estudiante);

            LOG.debugf("@guardarEstudianteEnMongoValidatorManual API> Finaliza ejecución del servicio");

            return Response.status(Response.Status.CREATED).entity(respuesta).build();
        } else {
            throw  new VCException(400, violations.stream()
                    .map(err -> err.getMessage())
                    .collect(Collectors.joining(";")), null);
        }


    }

    @POST
    @Path("/validator-service")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Guardar_Estudiante_Validator_Service")
    @Operation(
            summary = "Guardar estudiante",
            description = "Permite el guardado de un estudiante en mongo realizando comprobación de validators " +
                    "en el servicio"
    )
    public Response guardarEstudianteEnMongoValidatorService(Estudiante estudiante) {

        LOG.debugf("@guardarEstudianteEnMongoValidatorService API> Inicia ejecución del servicio");

        RespuestaGuardadoMongo respuesta = estudianteService.guardarEstudianteMongoValidatorService(estudiante);

        LOG.debugf("@guardarEstudianteEnMongoValidatorService API> Finaliza ejecución del servicio");

        return Response.status(Response.Status.CREATED).entity(respuesta).build();
    }

}
