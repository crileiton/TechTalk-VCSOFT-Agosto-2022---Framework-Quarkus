package org.quarkus.vcsoft.helpers.exception;

import org.jboss.logging.Logger;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class VCExceptionResponseHandler implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(VCExceptionResponseHandler.class);

    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(Throwable exception) {
        if (exception instanceof VCException) {
            VCException ex = (VCException) exception;
            Problem prlm = ex.getProblem();
            LOG.errorf(exception,"Se reporta Problema en respuesta REST para excepción VC %s ", prlm);
            return Response.status(ex.getStatus()).entity(prlm).build();
        }

        if (exception instanceof WebApplicationException) {
            Problem prlm = new Problem();
            prlm.setDetail(exception.getMessage());
            prlm.setTitle("Error en microservicio: micro-backend-for-frontend");
            prlm.setType("http://localhost:9001/bff/error/unknown");
            prlm.setInstance("Instancia micro-backend-for-frontend");
            LOG.errorf(exception,"Se reporta Problema en respuesta REST para excepción VC %s ", prlm);
            return Response.status(((WebApplicationException) exception).getResponse().getStatus()).entity(prlm).build();
        }
        // Agregar I/O Exception

        Problem prlm = new Problem();
        prlm.setDetail(exception.getMessage());
        prlm.setTitle("Error en microservicio: micro-backend-for-frontend");
        prlm.setType("http://localhost:9001/bff/error/unknown");
        prlm.setInstance("Instancia micro-backend-for-frontend");

        LOG.errorf(exception,
                "Se reporta Problema en respuesta REST para excepción no definida por VC %s ",
                prlm);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(prlm)
                .build();
    }
}