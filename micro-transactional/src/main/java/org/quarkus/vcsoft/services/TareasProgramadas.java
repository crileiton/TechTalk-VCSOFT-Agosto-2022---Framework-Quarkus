package org.quarkus.vcsoft.services;

import io.quarkus.scheduler.Scheduled;
import org.jboss.logging.Logger;
import org.quarkus.vcsoft.repositories.LibroRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class TareasProgramadas {

    private static final Logger LOG = Logger.getLogger(TareasProgramadas.class);

    @Inject
    LibroRepository libroRepository;

    @Transactional
    @Scheduled( cron = "{tarea-programada.uno.cron.expr}", identity = "tarea-programada-uno")
    public void tareaProgramadaUno(){
        LOG.infof("@tareaProgramadaUno SERV > Inicia ejecución de tarea programada uno");
        Long totalLibros = libroRepository.obtenerTodosLosLibros();
        LOG.infof("@tareaProgramadaUno SERV > Finaliza ejecución de tarea programada uno. Total de libros " +
                "encontrados: %d", totalLibros);

    }

}
