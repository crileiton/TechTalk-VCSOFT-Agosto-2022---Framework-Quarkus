package org.quarkus.vcsoft.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quarkus.vcsoft.helpers.ValidationGroups;
import org.quarkus.vcsoft.helpers.validacionesPersonalizadas.Age;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@MongoEntity(collection="Estudiante")
@Age
public class Estudiante {

    @Null(message = "No debe ingresar el código del estudiante")
    private String codigo;

    @NotBlank(message = "Debe ingresar el tipo de documento del estudiante")
    private String tipoDocumento;

    @NotBlank(message = "Debe ingresar el número del documento del estudiante")
    @Size(min = 7, max = 10, message = "El número del documento debe tener una longitud entre 7 y 10 caracteres")
    private String numeroDocumento;

    @NotBlank(message = "Debe ingresar los nombre del estudiante")
    @Size(min = 3, max = 60, message = "Los nombres deben tener una longitud entre 3 y 60 caracteres")
    private String nombres;

    @NotBlank(message = "Debe ingresar los apellidos del estudiante")
    @Size(min = 3, max = 60, message = "Los apellidos deben tener una longitud entre 3 y 60 caracteres")
    private String apellidos;

    @NotNull(message = "Debe ingresar la edad del estudiante")
    @Min(value = 15, message = "La edad del estudiante debe ser mayor a 15")
    @Max(value = 25, message = "La edad del estudiante debe ser menor a 25")
    private int edad;

    @Past(message = "La fecha de nacimiento del estudiante debe ser una fecha pasada")
    @NotNull(message = "Debe ingresar la fecha de nacimiento del estudiante")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
    private Date fechaNacimiento;

    private int numeroNotas;

    @NotNull(groups = {ValidationGroups.Post.class, ValidationGroups.Put.class}, message = "Debe ingresar la información de contacto del estudiante")
    @Valid
    private InformacionDeContacto informacionDeContacto;

}
