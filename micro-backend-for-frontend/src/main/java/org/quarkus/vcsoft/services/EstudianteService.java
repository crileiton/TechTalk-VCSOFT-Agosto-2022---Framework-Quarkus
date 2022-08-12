package org.quarkus.vcsoft.services;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.quarkus.vcsoft.domain.Estudiante;
import org.quarkus.vcsoft.domain.InformacionDeContacto;
import org.quarkus.vcsoft.helpers.RespuestaGuardadoMongo;
import org.quarkus.vcsoft.helpers.ValidationGroups;
import org.quarkus.vcsoft.restClient.TransaccionalEstudianteRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;

@ApplicationScoped
public class EstudianteService {

    private static final Logger LOG = Logger.getLogger(EstudianteService.class);

    @Inject
    @RestClient
    TransaccionalEstudianteRestClient estudianteRestClient;

    public RespuestaGuardadoMongo guardarEstudianteMongo(Estudiante estudiante){
        LOG.infof("@guardarEstudianteMongo SERV > Inicia ejecución de servicio. Rest client");
        RespuestaGuardadoMongo res = estudianteRestClient.guardarEstudianteMongo(estudiante);
        LOG.infof("@guardarEstudianteMongo SERV > Finaliza ejecución de servicio. Rest client");
        return res;
    }

    public RespuestaGuardadoMongo guardarEstudianteMongoValidatorService(
            @Valid @ConvertGroup(to = ValidationGroups.Post.class) Estudiante estudiante){
        LOG.infof("@guardarEstudianteMongoValidatorService SERV > Inicia ejecución de servicio. Rest client");
        RespuestaGuardadoMongo res = estudianteRestClient.guardarEstudianteMongo(estudiante);
        LOG.infof("@guardarEstudianteMongoValidatorService SERV > Finaliza ejecución de servicio. Rest client");
        return res;
    }

    public void actualizarInformacionContactoEstudiante(InformacionDeContacto informacionDeContacto, String idEstudiante){
        LOG.infof("@actualizarEstudianteMongo SERV > Inicia ejecución de servicio. Rest client");
        estudianteRestClient.actualizarInfoContactoDeEstudiante(idEstudiante, informacionDeContacto);
        LOG.infof("@actualizarEstudianteMongo SERV > Finaliza ejecución de servicio. Rest client");
    }

    public Estudiante obtenerEstudianteMongo(String idEstudiante){
        LOG.infof("@obtenerEstudianteMongo SERV > Inicia ejecución de servicio. Rest client");
        Estudiante obtenido = estudianteRestClient.obtenerEstudiantePorId(idEstudiante);
        LOG.infof("@obtenerEstudianteMongo SERV > Finaliza ejecución de servicio. Rest client");
        return obtenido;
    }

    public Estudiante obtenerEstudianteMongoCircuit(String idEstudiante){
        LOG.infof("@obtenerEstudianteMongo SERV > Inicia ejecución de servicio. Rest client");
        Estudiante obtenido = estudianteRestClient.obtenerEstudiantePorIdCircuit(idEstudiante);
        LOG.infof("@obtenerEstudianteMongo SERV > Finaliza ejecución de servicio. Rest client");
        return obtenido;
    }

}
