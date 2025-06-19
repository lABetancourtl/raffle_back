package com.aba.raffle.proyecto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ThreePackagesOnlyValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreePackagesOnly {
    String message() default "Debes definir exactamente 3 paquetes";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
