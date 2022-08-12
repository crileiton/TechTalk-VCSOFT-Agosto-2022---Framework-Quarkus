package org.quarkus.vcsoft.services;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import org.jboss.logging.Logger;
import org.quarkus.vcsoft.domain.Estudiante;
import org.quarkus.vcsoft.domain.Resultado;
import org.quarkus.vcsoft.helpers.RespuestaGuardadoMongo;
import org.quarkus.vcsoft.helpers.exception.VCException;
import org.quarkus.vcsoft.repositories.EstudianteRepository;
import org.quarkus.vcsoft.repositories.ResultadoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@ApplicationScoped
public class ResultadoService {

    private static final Logger LOG = Logger.getLogger(ResultadoService.class);

    private AtomicLong counter = new AtomicLong(0);

    @Inject
    EstudianteRepository estudianteRepository;

    @Inject
    ResultadoRepository resultadoRepository;

    @Inject
    MongoService mongoService;

    @Inject
    MongoClient mongoClient;

    public void guardarResultadoMongo(Resultado resultado) throws VCException {
        final Long invocationNumber = counter.getAndIncrement();
        LOG.infof("@guardarResultadoMongo SERV > Inicia ejecución de servicio para actualización de promedio " +
                "para estudiante con código: %s, invocation #%d", resultado.getCodigoEstudiante(), invocationNumber);

        Optional<Estudiante> estudiante = estudianteRepository.obtenerEstudiantePorId(resultado.getCodigoEstudiante());

        if(estudiante.isPresent()){
            LOG.infof("@guardarResultadoMongo SERV > El estudiante con código: %s fue encontrado. Inicia " +
                    "transacción mongo. invocation #%d", resultado.getCodigoEstudiante(), invocationNumber);

            Optional<Resultado> acumulado = resultadoRepository.obtenerResultadoPorIdEstudiante(resultado.getCodigoEstudiante());

            if(acumulado.isPresent()){
                ClientSession clientSession = mongoClient.startSession();
                clientSession.startTransaction();

                try{

                    LOG.infof("@guardarResultadoMongo SERV > Inicia actualización del número de notas del estudiante " +
                            "con código: %s. invocation #%d", resultado.getCodigoEstudiante(), invocationNumber);

                    long actualizarEstudiante = mongoService.getCollectionEstudiante().updateOne(clientSession,
                            combine(eq("codigo", resultado.getCodigoEstudiante())),
                            combine(set("numeroNotas", estudiante.get().getNumeroNotas() + 1))
                    ).getMatchedCount();

                    LOG.infof("@guardarResultadoMongo SERV > Inicia actualización del promedio del estudiante " +
                            "con código: %s. invocation #%d", resultado.getCodigoEstudiante(), invocationNumber);

                    double nuevoPromedio = (acumulado.get().getPromedio() + resultado.getPromedio()) / (estudiante.get().getNumeroNotas() + 1);

                    long actualizarPromedio = mongoService.getCollectionResultados().updateOne(clientSession,
                            combine(eq("codigoEstudiante", resultado.getCodigoEstudiante())),
                            combine(set("promedio", nuevoPromedio))
                    ).getMatchedCount();

                    if(actualizarEstudiante == 1 && actualizarPromedio == 1){
                        maybeFail();
                        LOG.infof("@guardarResultadoMongo SERV > Las actualizaciones se ejecutaron correctamente " +
                                "para el estudiante con código: %s. Commit transaction. invocation #%d",
                                resultado.getCodigoEstudiante(), invocationNumber);
                        clientSession.commitTransaction();
                    }else{
                        LOG.infof("@guardarResultadoMongo SERV > Algo salió mal en la actualización " +
                                "para el estudiante con código: %s. Abort transaction. invocation #%d",
                                resultado.getCodigoEstudiante(), invocationNumber);
                        clientSession.abortTransaction();
                    }
                }catch (Exception ex){
                    LOG.errorf(ex, "@guardarResultadoMongo SERV > Ocurrió un error actualizando el promedio " +
                            "para el estudiante con código: %s. Abort transaction. invocation #%d",
                            resultado.getCodigoEstudiante(), invocationNumber);
                    clientSession.abortTransaction();
                    throw new VCException(500, "Ocurrió un error actualizando el promedio", null);
                }finally {
                    LOG.infof("@guardarResultadoMongo SERV > Termina ejecución del método para actualizar el promedio " +
                            "para el estudiante con código: %s. Se cierra Client Session. invocation #%d",
                            resultado.getCodigoEstudiante(), invocationNumber);
                    clientSession.close();
                }
            }else{
                throw new VCException(404, "Resultado acumulado no encontrado", null);
            }
        }else{
            throw new VCException(404, "Estudiante no encontrado", null);
        }

    }

    private void maybeFail() throws VCException {
        if (new Random().nextBoolean()) {
            LOG.warn("@maybeFail SERV > Ejecución del servicio va a fallar.");
            throw new VCException(500, "Error guardando resultado intencional", null);
        }else{
            LOG.info("@maybeFail SERV > Ejecución del servicio se ejecuta normalmente.");
        }
    }

}
