package com.branches.utils;

import com.branches.exception.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class ValidateFullName {
    public void execute(String name) {
        if (name.split(" ").length < 2) {
            throw new BadRequestException("O nome completo deve conter pelo menos nome e sobrenome");
        }
    }
}
