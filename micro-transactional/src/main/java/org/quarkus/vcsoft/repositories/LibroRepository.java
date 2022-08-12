package org.quarkus.vcsoft.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.quarkus.vcsoft.domain.Libro;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.Optional;

@ApplicationScoped
public class LibroRepository implements PanacheRepository<Libro> {

    public void guardarLibro(Libro libro){
        persist(libro);
    }

    public Optional<Libro> obtenerLibroPorId(Long idLibro){
        return find("id = ?1", idLibro).firstResultOptional();
    }

    public Long obtenerTodosLosLibros(){
        return count();
    }

}
