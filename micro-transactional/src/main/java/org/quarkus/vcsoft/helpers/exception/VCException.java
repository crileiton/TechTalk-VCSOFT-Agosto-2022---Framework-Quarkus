package org.quarkus.vcsoft.helpers.exception;


import javax.ws.rs.core.Response;

public class VCException extends Exception {
    private Response.Status status;

    public VCException(int statusCode, String msg, Throwable t) {
        super(msg, t);
        this.status = Response.Status.fromStatusCode(statusCode);
    }

    public Problem getProblem() {
        return Problem.builder().
                detail(this.getMessage()).
                title(this.status.getReasonPhrase()).
                type("http://localhost:9001/bff/error/request").build();
    }

    public void setStatus(Response.Status status) {
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }
}
