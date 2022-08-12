package org.quarkus.vcsoft.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.quarkus.vcsoft.domain.Estudiante;
import org.quarkus.vcsoft.domain.Resultado;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class ResultadoRepository implements PanacheMongoRepository<Resultado> {

    public void guardarResultado(Resultado resultado){
        persist(resultado);
    }

    public Optional<Resultado> obtenerResultadoPorIdEstudiante(String idEstudiante){
        return find("codigoEstudiante = ?1", idEstudiante).firstResultOptional();
    }
}
