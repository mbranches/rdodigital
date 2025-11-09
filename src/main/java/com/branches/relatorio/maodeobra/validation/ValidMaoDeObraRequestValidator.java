package com.branches.relatorio.maodeobra.validation;

import com.branches.obra.domain.enums.TipoMaoDeObra;
import com.branches.relatorio.maodeobra.dto.request.CreateMaoDeObraRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidMaoDeObraRequestValidator implements ConstraintValidator<ValidMaoDeObraRequest, CreateMaoDeObraRequest> {

    @Override
    public boolean isValid(CreateMaoDeObraRequest value, ConstraintValidatorContext context) {
        if (value == null) return true;

        boolean personalizada = value.tipo() == TipoMaoDeObra.PERSONALIZADA;
        boolean nomePreenchido = value.nome() != null && !value.nome().isBlank();

        if (personalizada && !nomePreenchido) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("O campo 'nome' é obrigatório quando o tipo é 'PERSONALIZADA'.")
                   .addPropertyNode("nome")
                   .addConstraintViolation();
            return false;
        }
        return true;
    }
}

