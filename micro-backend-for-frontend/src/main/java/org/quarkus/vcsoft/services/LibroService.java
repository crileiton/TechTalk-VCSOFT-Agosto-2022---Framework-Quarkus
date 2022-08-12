package org.quarkus.vcsoft.services;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.quarkus.vcsoft.domain.Libro;
import org.quarkus.vcsoft.helpers.RespuestaGuardadoOracle;
import org.quarkus.vcsoft.restClient.TransaccionalLibroRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class LibroService {

    private static final Logger LOG = Logger.getLogger(LibroService.class);

    @Inject
    @RestClient
    TransaccionalLibroRestClient libroRestClient;

    public RespuestaGuardadoOracle guardarLibroAnnotationTransactionalOracle(Libro libro){
        LOG.infof("@guardarLibroAnnotationTransactionalOracle SERV > Inicia ejecución de servicio. Rest client");
        RespuestaGuardadoOracle res = libroRestClient.guardarLibroAnnotationTransactionalOracle(libro);
        LOG.infof("@guardarLibroAnnotationTransactionalOracle SERV > Finaliza ejecución de servicio. Rest client");
        return res;
    }

    public RespuestaGuardadoOracle guardarLibroQuarkusTransaccionalOracle(Libro libro){
        LOG.infof("@guardarLibroQuarkusTransaccionalOracle SERV > Inicia ejecución de servicio. Rest client");
        RespuestaGuardadoOracle res = libroRestClient.guardarLibroQuarkusTransaccionalOracle(libro);
        LOG.infof("@guardarLibroQuarkusTransaccionalOracle SERV > Finaliza ejecución de servicio. Rest client");
        return res;
    }

    public Libro obtenerLibroOracle(Long idLibro){
        LOG.infof("@guardarEstudianteMongo SERV > Inicia ejecución de servicio. Rest client");
        Libro libro = libroRestClient.obtenerLibroPorId(idLibro);
        LOG.infof("@guardarEstudianteMongo SERV > Finaliza ejecución de servicio. Rest client");
        return libro;
    }

}
