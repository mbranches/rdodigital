package com.branches.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermissionsDefault {
    private Boolean canCreateAndEdit;
    private Boolean canDelete;

    public static PermissionsDefault fullPermissions() {
        return new PermissionsDefault(
                true,
                true
        );
    }
}
