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

@Path("transaccion/estudiante")
@RegisterRestClient(configKey = "transactional")
@RegisterProvider(value = RuntimeResponseHandler.class)
public interface TransaccionalEstudianteRestClient {


    @POST
    @Path("/guardar/validator-endpoint")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timeout(250L)
    @Retry(maxRetries = 3, delay = 1000L)
    RespuestaGuardadoMongo guardarEstudianteMongo(@RequestBody Estudiante estudiante);

    @POST
    @Path("/guardar/validator-service")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timeout(250L)
    @Retry(maxRetries = 3, delay = 1000L)
    RespuestaGuardadoMongo guardarEstudianteMongoValidatorService(@RequestBody Estudiante estudiante);

    @GET
    @Path("/obtener/{idEstudiante}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timeout(500L)
    @Retry(maxRetries = 3, delay = 1000L)
    Estudiante obtenerEstudiantePorId(@PathParam("idEstudiante") String idEstudiante);

    @GET
    @Path("/obtener/{idEstudiante}/circuit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Estudiante obtenerEstudiantePorIdCircuit(@PathParam("idEstudiante") String idEstudiante);

    @PUT
    @Path("/actualizar/{idEstudiante}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Timeout(250L)
    @Retry(maxRetries = 3, delay = 1000L)
    void actualizarInfoContactoDeEstudiante(@PathParam("idEstudiante") String idEstudiante,
                                         @RequestBody InformacionDeContacto informacionDeContacto);

}
