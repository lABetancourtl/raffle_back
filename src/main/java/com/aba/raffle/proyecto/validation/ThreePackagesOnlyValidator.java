package com.aba.raffle.proyecto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ThreePackagesOnlyValidator implements ConstraintValidator<ThreePackagesOnly, List<?>> {
    @Override
    public boolean isValid(List<?> paquetes, ConstraintValidatorContext context) {
        return paquetes != null && paquetes.size() == 3;
    }
}
