package org.quarkus.vcsoft.services;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.jboss.logging.Logger;
import org.quarkus.vcsoft.domain.Estudiante;
import org.quarkus.vcsoft.domain.InformacionDeContacto;
import org.quarkus.vcsoft.domain.Resultado;
import org.quarkus.vcsoft.helpers.RespuestaGuardadoMongo;
import org.quarkus.vcsoft.helpers.ValidationGroups;
import org.quarkus.vcsoft.helpers.exception.VCException;
import org.quarkus.vcsoft.repositories.EstudianteRepository;
import org.quarkus.vcsoft.repositories.ResultadoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@ApplicationScoped
public class EstudianteService {

    private static final Logger LOG = Logger.getLogger(EstudianteService.class);

    private AtomicLong counter = new AtomicLong(0);
    private AtomicLong counterCircuitBreaker = new AtomicLong(0);

    @Inject
    EstudianteRepository estudianteRepository;

    @Inject
    ResultadoRepository resultadoRepository;

    @Inject
    MongoService mongoService;

    public RespuestaGuardadoMongo guardarEstudianteMongo(Estudiante estudiante){
        final Long invocationNumber = counter.getAndIncrement();
        LOG.infof("@guardarEstudianteMongo SERV > Inicia ejecución de servicio. invocation #%d", invocationNumber);
        UUID codigoEstudiante = UUID.randomUUID();
        estudiante.setCodigo(codigoEstudiante.toString());
        estudiante.setNumeroNotas(0);
        estudianteRepository.guardarEstudiante(estudiante);
        resultadoRepository.guardarResultado(Resultado.builder().codigoEstudiante(codigoEstudiante.toString())
                .promedio(0).build());
        LOG.infof("@guardarEstudianteMongo SERV > Finaliza ejecución de servicio. invocation #%d", invocationNumber);
        return RespuestaGuardadoMongo.builder().uuid(codigoEstudiante).build();
    }

    public RespuestaGuardadoMongo guardarEstudianteMongoValidatorService(@Valid @ConvertGroup(to = ValidationGroups.Post.class)
                                                                         Estudiante estudiante){
        final Long invocationNumber = counter.getAndIncrement();
        LOG.infof("@guardarEstudianteMongoValidatorService SERV > Inicia ejecución de servicio. invocation #%d",
                invocationNumber);
        UUID codigoEstudiante = UUID.randomUUID();
        estudiante.setCodigo(codigoEstudiante.toString());
        estudiante.setNumeroNotas(0);
        estudianteRepository.guardarEstudiante(estudiante);
        resultadoRepository.guardarResultado(Resultado.builder().codigoEstudiante(codigoEstudiante.toString())
                .promedio(0).build());
        LOG.infof("@guardarEstudianteMongoValidatorService SERV > Finaliza ejecución de servicio. invocation #%d",
                invocationNumber);
        return RespuestaGuardadoMongo.builder().uuid(codigoEstudiante).build();
    }

    public void actualizarInformacionContactoEstudiante(InformacionDeContacto informacionDeContacto, String idEstudiante)
            throws VCException {
        final Long invocationNumber = counter.getAndIncrement();
        LOG.infof("@actualizarInformacionContactoEstudiante SERV > Inicia ejecución de servicio. " +
                "invocation #%d", invocationNumber);
        long actualizarInfoContactoEstudiante = mongoService.getCollectionEstudiante().updateOne(
                combine(eq("codigo", idEstudiante)),
                combine(set("informacionDeContacto", informacionDeContacto))
        ).getMatchedCount();
        if(actualizarInfoContactoEstudiante == 1){
            LOG.infof("@actualizarInformacionContactoEstudiante SERV > La información de contacto del " +
                    "estudiante se actualizó correctamente. invocation #%d", invocationNumber);
        }else{
            throw new VCException(500, "Error actualizando información de contacto", null);
        }
        LOG.infof("@actualizarInformacionContactoEstudiante SERV > Finaliza ejecución de servicio. " +
                "invocation #%d.", invocationNumber);
    }

    public Estudiante obtenerEstudianteMongo(String idEstudiante) throws VCException, InterruptedException {
        final Long invocationNumber = counter.getAndIncrement();
        LOG.infof("@obtenerEstudianteMongo SERV > Inicia ejecución de servicio. invocation #%d", invocationNumber);
        randomDelay();
        Optional<Estudiante> estudiante = estudianteRepository.obtenerEstudiantePorId(idEstudiante);
        if(estudiante.isPresent()){
            Estudiante estudianteData = estudiante.get();
            LOG.infof("@obtenerEstudianteMongo SERV > Finaliza ejecución de servicio. invocation #%d",
                    invocationNumber);
            return estudianteData;
        }else{
            throw new VCException(404, "Estudiante no encontrado", null);
        }

    }

    @CircuitBreaker(requestVolumeThreshold = 4, delay = 10000L)
    public Estudiante obtenerEstudianteMongoCircuit(String idEstudiante) throws VCException {
        final Long invocationNumber = counterCircuitBreaker.getAndIncrement();
        LOG.infof("@obtenerEstudianteMongo SERV > Inicia ejecución de servicio. invocation #%d", invocationNumber);
        maybeFail(invocationNumber);
        Optional<Estudiante> estudiante = estudianteRepository.obtenerEstudiantePorId(idEstudiante);
        if(estudiante.isPresent()){
            Estudiante estudianteData = estudiante.get();
            LOG.infof("@obtenerEstudianteMongo SERV > Finaliza ejecución de servicio. invocation #%d",
                    invocationNumber);
            return estudianteData;
        }else{
            throw new VCException(404, "Estudiante no encontrado", null);
        }
    }

    private void maybeFail(long invocationNumber) throws VCException {
        LOG.infof("@obtenerEstudianteMongo SERV > maybe fail. invocation #%d", invocationNumber);
        LOG.infof("@obtenerEstudianteMongo SERV > %d / 4 = %d", invocationNumber, invocationNumber%4);
        if (invocationNumber % 4 > 1) { // alternate 2 successful and 2 failing invocations
            throw new VCException(500, "Error obteniendo estudiante CIRCUIT", null);
        }
    }

    private void randomDelay() throws InterruptedException {
        int sleepTime = new Random().nextInt(500);
        LOG.infof("@randomDelay SERV > El slepp time es: %d", sleepTime);
        Thread.sleep(sleepTime);
    }

}
