package xyz.needpainkiller.api.tenant.adapter.out.persistence.mapper;

import org.mapstruct.Mapper;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.entity.TenantEntity;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;


/**
 * 테넌트 매퍼
 * - Persistence -> Domain / Domain -> Persistence
 *
 * @author needpainkiller6512
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface TenantPersistenceMapper {
    /**
     * 테넌트 Entity -> Domain
     *
     * @param tenant 테넌트
     * @return TenantEntity 테넌트 Entity
     */
    TenantEntity toTenantEntity(Tenant tenant);

    /**
     * 테넌트 Entity -> Domain
     *
     * @param tenantEntity 테넌트 Entity
     * @return Tenant 테넌트
     */
    Tenant toTenant(TenantEntity tenantEntity);
}
