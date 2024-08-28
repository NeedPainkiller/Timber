package xyz.needpainkiller.api.user_hex.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import xyz.needpainkiller.api.tenant.domain.model.TenantBase;
import xyz.needpainkiller.lib.jpa.BooleanConverter;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
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
public class Role implements GrantedAuthority, Serializable, TenantBase {
    @Serial
    private static final long serialVersionUID = -1718921928627969489L;

    private Long id;
    private Long tenantPk;
    private boolean useYn;
    private boolean isSystemAdmin;
    private boolean isAdmin;
    private boolean isEditable;
    private String roleName;
    private String roleDescription;
    private Long createdBy;
    private Timestamp createdDate;
    private Long updatedBy;
    private Timestamp updatedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return useYn == role.useYn && isSystemAdmin == role.isSystemAdmin && isAdmin == role.isAdmin && isEditable == role.isEditable && Objects.equals(id, role.id) && Objects.equals(tenantPk, role.tenantPk) && Objects.equals(roleName, role.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantPk, useYn, roleName, isSystemAdmin, isAdmin, isEditable);
    }


    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", tenantPk=" + tenantPk +
                ", useYn=" + useYn +
                ", isSystemAdmin=" + isSystemAdmin +
                ", isAdmin=" + isAdmin +
                ", isEditable=" + isEditable +
                ", roleName='" + roleName + '\'' +
                ", roleDescription='" + roleDescription + '\'' +
                ", createdBy=" + createdBy +
                ", createdDate=" + createdDate +
                ", updatedBy=" + updatedBy +
                ", updatedDate=" + updatedDate +
                '}';
    }

    @Override
    public String getAuthority() {
        return roleName;
    }

    public boolean isAvailable() {
        return this.useYn;
    }

    public boolean isSystemAdmin() {
        return this.isSystemAdmin;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public boolean isEditable() {
        return this.isEditable;
    }

}
