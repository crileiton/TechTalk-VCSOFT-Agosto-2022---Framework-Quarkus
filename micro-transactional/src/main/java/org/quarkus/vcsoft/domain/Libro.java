package org.quarkus.vcsoft.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(message = "No debe enviar el id del libro")
    private Long id;

    @NotBlank(message = "Debe ingresar el código ISBN del Libro")
    @Size(min = 10, max = 13, message = "El código ISBN debe tener una longitud entre 10 y 13 caracteres")
    private String codigoISBN;

    @NotBlank(message = "Debe ingresar el título del Libro")
    @Size(min = 5, max = 60, message = "El título debe tener una longitud entre 5 y 60 caracteres")
    private String titulo;

    @NotBlank(message = "Debe ingresar el autor del Libro")
    @Size(min = 5, max = 60, message = "El nombre del autor debe tener una longitud entre 5 y 60 caracteres")
    private String autor;

}
