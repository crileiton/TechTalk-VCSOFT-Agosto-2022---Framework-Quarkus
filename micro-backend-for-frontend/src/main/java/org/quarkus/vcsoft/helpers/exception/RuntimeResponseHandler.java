package org.quarkus.vcsoft.helpers.exception;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class RuntimeResponseHandler implements ResponseExceptionMapper<RuntimeException> {

    @Override
    public RuntimeException toThrowable(Response response) {
        int status = response.getStatus();
        String msg = getBody(response);
        RuntimeException re = new WebApplicationException(msg, status);
        return re;
    }

    private String getBody(Response response) {
       Problem prm = response.readEntity(Problem.class);
        return prm.getDetail();
    }
}

