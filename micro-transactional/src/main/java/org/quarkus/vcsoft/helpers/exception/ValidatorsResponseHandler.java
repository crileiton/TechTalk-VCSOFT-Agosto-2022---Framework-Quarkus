package org.quarkus.vcsoft.helpers.exception;

import org.jboss.logging.Logger;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

@Provider
public class ValidatorsResponseHandler implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOG = Logger.getLogger(ValidatorsResponseHandler.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        Problem respuesta = Problem.builder()
                .instance("Instancia ve-portal")
                .type("http://localhost:9001/bff/error/unknown")
                .detail(exception.getConstraintViolations().stream()
                        .map(err -> err.getMessage())
                        .collect(Collectors.joining(";")))
                .title("Error en microservicio: micro-backend-for-frontend (ConstraintViolationException)").build();

        LOG.errorf(exception,
                "Se reporta Problema en respuesta REST para excepción no definida por VC %s ",
                respuesta);

        return Response.status(Response.Status.BAD_REQUEST).entity(respuesta)
                .type(MediaType.APPLICATION_JSON).build();
    }

}