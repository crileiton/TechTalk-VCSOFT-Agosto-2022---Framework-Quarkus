package org.quarkus.vcsoft.restClient;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.quarkus.vcsoft.domain.Resultado;
import org.quarkus.vcsoft.helpers.exception.RuntimeResponseHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("transaccion/resultado")
@RegisterRestClient(configKey = "transactional")
@RegisterProvider(value = RuntimeResponseHandler.class)
public interface TransaccionalResultadoRestClient {

    @PUT
    @Path("/guardar")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    void guardarResultadoEstudiante(@RequestBody Resultado resultado);

}
