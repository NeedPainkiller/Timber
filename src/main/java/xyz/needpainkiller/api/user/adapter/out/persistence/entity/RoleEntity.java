package xyz.needpainkiller.api.user.adapter.out.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
 import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
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
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicUpdate
@Table(name = "ACCOUNT_ROLE")
public class RoleEntity implements GrantedAuthority, Serializable, TenantBase {
    @Serial
    private static final long serialVersionUID = 517080619145517610L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_ID", unique = true, nullable = false, columnDefinition = "bigint")
    private Long id;
    @Column(name = "TENANT_PK", nullable = false, columnDefinition = "bigint default 0")
    private Long tenantPk;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "USE_YN", nullable = false, columnDefinition = "tinyint unsigned default 0")
    private boolean useYn;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "IS_SYSTEM_ADMIN", nullable = false, columnDefinition = "tinyint unsigned default 0")
    private boolean isSystemAdmin;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "IS_ADMIN", nullable = false, columnDefinition = "tinyint unsigned default 0")
    private boolean isAdmin;
    @Convert(converter = BooleanConverter.class)
    @Column(name = "IS_EDITABLE", nullable = false, columnDefinition = "tinyint unsigned default 1")
    private boolean isEditable;
    @Column(name = "ROLE_NAME", nullable = false, columnDefinition = "nvarchar(256)")
    private String roleName;
    @Column(name = "ROLE_DESCRIPTION", nullable = true, columnDefinition = "nvarchar(1024) default null")
    private String roleDescription;
    @Column(name = "CREATED_BY", nullable = false, columnDefinition = "bigint default 0")
    private Long createdBy;
    @Column(name = "CREATED_DATE", nullable = false, columnDefinition = "datetime2(0) default CURRENT_TIMESTAMP")
    @CreationTimestamp
    private Timestamp createdDate;
    @Column(name = "UPDATED_BY", nullable = false, columnDefinition = "bigint default 0")
    private Long updatedBy;
    @Column(name = "UPDATED_DATE", nullable = false, columnDefinition = "datetime2(0) default CURRENT_TIMESTAMP")
    @UpdateTimestamp
    private Timestamp updatedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleEntity role = (RoleEntity) o;
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
