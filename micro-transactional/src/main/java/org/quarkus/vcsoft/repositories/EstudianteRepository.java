package org.quarkus.vcsoft.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.quarkus.vcsoft.domain.Estudiante;

import javax.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class EstudianteRepository implements PanacheMongoRepository<Estudiante> {

    public void guardarEstudiante(Estudiante estudiante){
        persist(estudiante);
    }

    public Optional<Estudiante> obtenerEstudiantePorId(String idEstudiante){
        return find("codigo = ?1", idEstudiante).firstResultOptional();
    }
}
