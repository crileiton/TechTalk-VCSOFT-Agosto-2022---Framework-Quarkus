package org.quarkus.vcsoft.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Resultado {

    @NotEmpty(message = "Debe ingresar el código del estudiante")
    private String codigoEstudiante;

    @Min(value = 0, message = "El valor mínimo para promedio es cero (0)")
    @Max(value = 5, message = "El valor máximo para promedio es cinco (5)")
    private double promedio;
    
}
