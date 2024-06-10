package xyz.needpainkiller.api.tenant.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.function.Predicate;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_NULL)
@JsonIgnoreProperties(value = {"hibernate_lazy_initializer", "handler"}, ignoreUnknown = true)
// https://stackoverflow.com/questions/67353793/what-does-jsonignorepropertieshibernatelazyinitializer-handler-do
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Tenant implements Serializable, TenantBase {
    @Serial
    private static final long serialVersionUID = 9094838141707046704L;

    private Long id;
    private boolean useYn;
    private boolean defaultYn;
    private boolean visibleYn;
    private String title;
    private String label;
    private String url;
    private Long createdBy;
    private Timestamp createdDate;
    private Long updatedBy;
    private Timestamp updatedDate;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant that = (Tenant) o;
        return defaultYn == that.defaultYn && useYn == that.useYn && visibleYn == that.visibleYn && Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(label, that.label) && Objects.equals(url, that.url) && Objects.equals(createdBy, that.createdBy) && Objects.equals(createdDate, that.createdDate) && Objects.equals(updatedBy, that.updatedBy) && Objects.equals(updatedDate, that.updatedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, defaultYn, useYn, visibleYn, title, label, url, createdBy, createdDate, updatedBy, updatedDate);
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + id +
                ", defaultYn=" + defaultYn +
                ", useYn=" + useYn +
                ", visibleYn=" + visibleYn +
                ", title='" + title + '\'' +
                ", label='" + label + '\'' +
                ", url='" + url + '\'' +
                ", createdBy=" + createdBy +
                ", createdDate=" + createdDate +
                ", updatedBy=" + updatedBy +
                ", updatedDate=" + updatedDate +
                '}';
    }


    public static final Predicate<Tenant> predicateAvailableTenant = Tenant::isActive;
    public static final Predicate<Tenant> predicatePublicTenant = Tenant::isPublic;
    public static final Predicate<Tenant> predicateDefaultTenant = Tenant::isDefault;

    public Long getTenantPk() {
        return id;
    }

    public void setTenantPk(Long tenantPk) {
        // do nothing
        // autoincrement id
        // update tenantPk of Tenant is not allowed
    }

    public boolean isActive() {
        return useYn;
    }

    public boolean isPublic() {
        return useYn && visibleYn;
    }


    public boolean isDefault() {
        return useYn && defaultYn;
    }
}
