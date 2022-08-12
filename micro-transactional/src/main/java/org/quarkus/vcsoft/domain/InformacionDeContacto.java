package org.quarkus.vcsoft.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class InformacionDeContacto {

    @NotBlank(message = "Debe ingresar la dirección en información de contacto")
    private String direccion;

    @NotBlank(message = "Debe ingresar el número de teléfono en información de contacto")
    @Pattern(regexp = "^(60[1245678])\\d{7}", message = "El formato del número de teléfono en información de contacto " +
            "debe ser 60 + código de región + 7 dígitos más")
    private String numeroTelefono;

    @NotBlank(message = "Debe ingresar el número de celular en información de contacto")
    @Pattern(regexp = "^(3)\\d{9}", message = "El formato del número de celular en información de contacto " +
            "debe empezar por 3 y tener 10 dígitos en total")
    private String numeroCelular;

    @NotBlank(message = "Debe ingresar el correo electrónico en información de contacto")
    @Email(message = "El formato del correo electrónico no es válido en información de contacto")
    private String correoElectronico;

}
