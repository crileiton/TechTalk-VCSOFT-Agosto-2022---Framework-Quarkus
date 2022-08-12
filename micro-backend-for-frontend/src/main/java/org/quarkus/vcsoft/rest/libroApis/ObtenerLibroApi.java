package org.quarkus.vcsoft.rest.libroApis;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.quarkus.vcsoft.domain.Libro;
import org.quarkus.vcsoft.helpers.exception.VCException;
import org.quarkus.vcsoft.services.LibroService;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/bff/libro/obtener")
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
            @PathParam("idLibro") Long idLibro) {

        LOG.debugf("@obtenerLibroPorId API> Inicia ejecución del servicio");

        Libro libro = libroService.obtenerLibroOracle(idLibro);

        LOG.debugf("@obtenerLibroPorId API> Finaliza ejecución del servicio");

        return Response.status(Response.Status.OK).entity(libro).build();
    }

}
