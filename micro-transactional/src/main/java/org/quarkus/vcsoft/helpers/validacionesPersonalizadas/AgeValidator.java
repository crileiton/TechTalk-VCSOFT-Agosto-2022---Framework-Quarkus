package org.quarkus.vcsoft.helpers.validacionesPersonalizadas;

import org.jboss.logging.Logger;
import org.quarkus.vcsoft.domain.Estudiante;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class AgeValidator implements ConstraintValidator<Age, Estudiante> {

    private static final Logger LOG = Logger.getLogger(AgeValidator.class);

    @Override
    public void initialize(Age constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Estudiante estudiante, ConstraintValidatorContext constraintValidatorContext) {
        LOG.debug("@isValid > Inicia validación para estudiante");

        // null values are valid
        if ( estudiante == null ) {
            LOG.debug("@isValid > El estudiante es nulo se retorna false");
            return false;
        }else if(estudiante.getFechaNacimiento() == null){
            LOG.debug("@isValid > La fecha de nacimiento del estudiante es nula");
            return false;
        }

        LocalDate today = LocalDate.now();

        long edadReal = (ChronoUnit.DAYS.between(estudiante.getFechaNacimiento().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate(), today) / 365);

        LOG.debugf("@isValid > La edad que debería tener es: %s", edadReal);
        return edadReal == estudiante.getEdad();
    }
}
