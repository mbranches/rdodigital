package com.branches.relatorio.maodeobra.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Constraint(validatedBy = com.branches.relatorio.maodeobra.validation.ValidMaoDeObraRequestValidator.class)
public @interface ValidMaoDeObraRequest {
    String message() default "O campo 'nome' é obrigatório quando o tipo é 'PERSONALIZADA'.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
