package com.branches.utils;

import org.springframework.stereotype.Component;

@Component
public class FullNameFormatter {
    public String execute(String fullName) {
        String nameWithoutExtraSpaces = fullName.trim().replaceAll("\\s+", " ");

        String[] nameParts = nameWithoutExtraSpaces.split(" ");

        StringBuilder formattedName = new StringBuilder();
        for (String part : nameParts) {
            formattedName.append(part.substring(0, 1).toUpperCase())
                         .append(part.substring(1).toLowerCase())
                         .append(" ");
        }

        return formattedName.toString().trim();
    }
}
