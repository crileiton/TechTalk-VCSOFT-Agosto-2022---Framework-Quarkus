package org.quarkus.vcsoft.services;

import io.quarkus.narayana.jta.QuarkusTransaction;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.jboss.logging.Logger;
import org.quarkus.vcsoft.domain.Libro;
import org.quarkus.vcsoft.helpers.RespuestaGuardadoOracle;
import org.quarkus.vcsoft.helpers.exception.VCException;
import org.quarkus.vcsoft.repositories.LibroRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class LibroService {

    private static final Logger LOG = Logger.getLogger(LibroService.class);

    private AtomicLong counter = new AtomicLong(0);

    @Inject
    LibroRepository libroRepository;

    @Transactional(rollbackOn = VCException.class)
    public RespuestaGuardadoOracle guardarLibroOracle(Libro libro) throws VCException {
        final Long invocationNumber = counter.getAndIncrement();
        LOG.infof("@guardarLibroOracle SERV > Inicia ejecución de servicio. invocation #%d", invocationNumber);
        libroRepository.guardarLibro(libro);
        maybeFail();
        LOG.infof("@guardarLibroOracle SERV > Finaliza ejecución de servicio. invocation #%d", invocationNumber);
        return RespuestaGuardadoOracle.builder().codigoLibro(libro.getId()).build();
    }

    public RespuestaGuardadoOracle guardarLibroTransaccional(Libro libro) throws VCException {
        final Long invocationNumber = counter.getAndIncrement();
        LOG.infof("@guardarLibroTransaccional SERV > Inicia ejecución de servicio. invocation #%d", invocationNumber);
        QuarkusTransaction.begin();

        libroRepository.guardarLibro(libro);
        if (!libro.getAutor().equals("pedro")) {
            QuarkusTransaction.commit();
            LOG.infof("@guardarLibroTransaccional SERV > Finaliza ejecución de servicio. Commit transaction. " +
                    "invocation #%d", invocationNumber);
            return RespuestaGuardadoOracle.builder().codigoLibro(libro.getId()).build();
        }
        else {
            QuarkusTransaction.rollback();
            LOG.infof("@guardarEstudianteMongo SERV > Finaliza ejecución de servicio. Rolback transaction. " +
                    "invocation #%d", invocationNumber);
            throw new VCException(500, "Error guardando libro autor pedro", null);
        }
    }

    @Fallback(fallbackMethod = "fallbackLibroDefault")
    public Libro obtenerLibroOracle(Long idLibro) throws VCException, InterruptedException {
        final Long invocationNumber = counter.getAndIncrement();
        LOG.infof("@obtenerLibroOracle SERV > Inicia ejecución de servicio. invocation #%d", invocationNumber);
        randomDelay();
        Optional<Libro> libro = libroRepository.obtenerLibroPorId(idLibro);
        if(libro.isPresent()){
            Libro libroData = libro.get();
            LOG.infof("@obtenerLibroOracle SERV > Finaliza ejecución de servicio. invocation #%d", invocationNumber);
            return libroData;
        }else{
            throw new VCException(404, "Libro no encontrado", null);
        }

    }

    public Libro fallbackLibroDefault(Long idLibro){

        final Long invocationNumber = counter.getAndIncrement();

        LOG.infof("@fallbackLibroDefault SERV > Se ejecuta fallback para método obtenerLibroOracle. " +
                "invocation #%d", invocationNumber);

        return Libro.builder()
                .id(-1L)
                .autor("Default")
                .codigoISBN("Default")
                .titulo("Default")
                .build();
    }

    private void maybeFail() throws VCException {
        if (new Random().nextBoolean()) {
            LOG.warn("@maybeFail SERV > Ejecución del servicio va a fallar. ");
            throw new VCException(500, "Error intencional guardando libro", null);
        }else{
            LOG.info("@maybeFail SERV > Ejecución del servicio se ejecuta normalmente.");
        }
    }

    private void randomDelay() throws InterruptedException {
        int sleepTime = new Random().nextInt(500);
        LOG.infof("@randomDelay SERV > El slepp time es: %d", sleepTime);
        Thread.sleep(sleepTime);
    }

}
