package org.quarkus.vcsoft.services;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.quarkus.vcsoft.domain.Resultado;
import org.quarkus.vcsoft.restClient.TransaccionalResultadoRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ResultadoService {

    private static final Logger LOG = Logger.getLogger(ResultadoService.class);

    @Inject
    @RestClient
    TransaccionalResultadoRestClient transaccionalRestClient;

    public void guardarResultadoEstudiante(Resultado resultado){
        LOG.infof("@guardarResultadoEstudiante SERV > Inicia ejecución de servicio. Rest client");
        transaccionalRestClient.guardarResultadoEstudiante(resultado);
        LOG.infof("@guardarResultadoEstudiante SERV > Finaliza ejecución de servicio. Rest client");
    }

}
