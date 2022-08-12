package org.quarkus.vcsoft.restClient;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.quarkus.vcsoft.domain.Estudiante;
import org.quarkus.vcsoft.domain.InformacionDeContacto;
import org.quarkus.vcsoft.domain.Libro;
import org.quarkus.vcsoft.domain.Resultado;
import org.quarkus.vcsoft.helpers.RespuestaGuardadoMongo;
import org.quarkus.vcsoft.helpers.RespuestaGuardadoOracle;
import org.quarkus.vcsoft.helpers.exception.RuntimeResponseHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("transaccion/libro")
@RegisterRestClient(configKey = "transactional")
@RegisterProvider(value = RuntimeResponseHandler.class)
public interface TransaccionalLibroRestClient {

    @POST
    @Path("/guardar/annotation-transactional")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    RespuestaGuardadoOracle guardarLibroAnnotationTransactionalOracle(@RequestBody Libro libro);

    @POST
    @Path("guardar/quarkus-transactional")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    RespuestaGuardadoOracle guardarLibroQuarkusTransaccionalOracle(@RequestBody Libro libro);

    @GET
    @Path("/obtener/{idLibro}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timeout(250L)
    @Retry(maxRetries = 3, delay = 1000L)
    Libro obtenerLibroPorId(@PathParam("idLibro") Long idLibro);
    
}
