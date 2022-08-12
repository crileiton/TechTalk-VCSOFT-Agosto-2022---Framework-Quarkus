package org.quarkus.vcsoft.rest.libroApis;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.quarkus.vcsoft.domain.Estudiante;
import org.quarkus.vcsoft.domain.Libro;
import org.quarkus.vcsoft.helpers.RespuestaGuardadoOracle;
import org.quarkus.vcsoft.helpers.ValidationGroups;
import org.quarkus.vcsoft.helpers.exception.Problem;
import org.quarkus.vcsoft.helpers.exception.VCException;
import org.quarkus.vcsoft.services.LibroService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transaccion/libro/obtener")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ObtenerLibroApi {

    private static final Logger LOG = Logger.getLogger(ObtenerLibroApi.class);

    @Inject
    LibroService libroService;

    @GET
    @Path("/{idLibro}")
    @Produces(MediaType.APPLICATION_JSON)
    @Tag(name = "Obtener_Libro")
    @Operation(
            summary = "Consultar un libro",
            description = "Se busca un libro con el id ingresado"
    )
    public Response obtenerLibroPorId(
            @Parameter(
                    description = "Id del Libro",
                    example = "1"
            )
            @NotNull(message = "Debe ingresar el id del libro")
            @PathParam("idLibro") Long idLibro) throws VCException, InterruptedException {

        LOG.debugf("@obtenerLibroPorId API> Inicia ejecución del servicio");

        Libro libro = libroService.obtenerLibroOracle(idLibro);

        LOG.debugf("@obtenerLibroPorId API> Finaliza ejecución del servicio");

        return Response.status(Response.Status.OK).entity(libro).build();
    }

}
