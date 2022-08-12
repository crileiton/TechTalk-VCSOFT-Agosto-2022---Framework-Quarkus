package org.quarkus.vcsoft.rest.libroApis;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.quarkus.vcsoft.domain.Libro;
import org.quarkus.vcsoft.helpers.RespuestaGuardadoOracle;
import org.quarkus.vcsoft.helpers.ValidationGroups;
import org.quarkus.vcsoft.helpers.exception.VCException;
import org.quarkus.vcsoft.services.LibroService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/bff/libro/guardar")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuardarLibroApi {

    private static final Logger LOG = Logger.getLogger(GuardarLibroApi.class);

    @Inject
    LibroService libroService;

    @POST
    @Path("/annotation-transactional")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Guardar_Libro_Annotation_Transational")
    @Operation(
            summary = "Guardar libro",
            description = "Permite el guardado de un libro en oracle aplicando transaccionalidad con anotación " +
                    "@Transactional"
    )
    public Response guardarLibroEnOracleAnotacionTransactional(
            @RequestBody(
                    description = "Contenido para el guardado del libro en oracle",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Libro.class))
            )
            @Valid @ConvertGroup(to = ValidationGroups.Post.class) Libro libro) {

        LOG.debugf("@guardarLibroEnOracleAnotacionTransactional API> Inicia ejecución del servicio");

        RespuestaGuardadoOracle respuestaGuardadoOracle = libroService.guardarLibroAnnotationTransactionalOracle(libro);

        LOG.debugf("@guardarLibroEnOracleAnotacionTransactional API> Finaliza ejecución del servicio");

        return Response.status(Response.Status.CREATED).entity(respuestaGuardadoOracle).build();
    }

    @POST
    @Path("/quarkus-transactional")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Guardar_Libro_Quarkus_Transactional")
    @Operation(
            summary = "Guardar libro",
            description = "Permite el guardado de un libro en oracle utilizando quarkus transactional"
    )
    public Response guardarLibroQuarkusTransactionalEnOracle(
            @RequestBody(
                    description = "Contenido para el guardado del libro en oracle",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Libro.class))
            )
            @Valid @ConvertGroup(to = ValidationGroups.Post.class) Libro libro) {

        LOG.debugf("@guardarLibroQuarkusTransactionalEnOracle API> Inicia ejecución del servicio");

        RespuestaGuardadoOracle respuestaGuardadoOracle = libroService.guardarLibroQuarkusTransaccionalOracle(libro);

        LOG.debugf("@guardarLibroQuarkusTransactionalEnOracle API> Finaliza ejecución del servicio");

        return Response.status(Response.Status.CREATED).entity(respuestaGuardadoOracle).build();
    }

}
