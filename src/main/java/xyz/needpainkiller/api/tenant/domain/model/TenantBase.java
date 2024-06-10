package xyz.needpainkiller.api.tenant.domain.model;

import java.util.function.Predicate;

public interface TenantBase {
    Long getTenantPk();

    void setTenantPk(Long tenantPk);

    Predicate<TenantBase> predicateTenantNotNull = tenants -> tenants.getTenantPk() != null;

    default boolean filterByTenant(Long tenantPk) {
        Long sourceTenantPk = getTenantPk();
        if (sourceTenantPk == null || tenantPk == null) {
            return false;
        }
        return sourceTenantPk.equals(tenantPk);
    }
}
