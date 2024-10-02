package xyz.needpainkiller.api.user.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(value = {"hibernate_lazy_initializer", "handler"}, ignoreUnknown = true)
@Cacheable
@IdClass(UserRoleMapId.class)
public class UserRoleMap implements Serializable {
    private static final long serialVersionUID = -4575954203895356191L;
    private Long userPk;
    private Long rolePk;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleMap userRole = (UserRoleMap) o;
        return Objects.equals(userPk, userRole.userPk) && Objects.equals(rolePk, userRole.rolePk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPk, rolePk);
    }
}

