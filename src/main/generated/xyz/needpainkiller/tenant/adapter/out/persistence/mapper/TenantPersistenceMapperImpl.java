package xyz.needpainkiller.tenant.adapter.out.persistence.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.entity.TenantEntity;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.mapper.TenantPersistenceMapper;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-12T20:08:22+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class TenantPersistenceMapperImpl implements TenantPersistenceMapper {

    @Override
    public TenantEntity toTenantEntity(Tenant tenant) {
        if ( tenant == null ) {
            return null;
        }

        TenantEntity.TenantEntityBuilder tenantEntity = TenantEntity.builder();

        tenantEntity.id( tenant.getId() );
        tenantEntity.useYn( tenant.isUseYn() );
        tenantEntity.defaultYn( tenant.isDefaultYn() );
        tenantEntity.visibleYn( tenant.isVisibleYn() );
        tenantEntity.title( tenant.getTitle() );
        tenantEntity.label( tenant.getLabel() );
        tenantEntity.url( tenant.getUrl() );
        tenantEntity.createdBy( tenant.getCreatedBy() );
        tenantEntity.createdDate( tenant.getCreatedDate() );
        tenantEntity.updatedBy( tenant.getUpdatedBy() );
        tenantEntity.updatedDate( tenant.getUpdatedDate() );

        return tenantEntity.build();
    }

    @Override
    public Tenant toTenant(TenantEntity tenantEntity) {
        if ( tenantEntity == null ) {
            return null;
        }

        Tenant tenant = new Tenant();

        tenant.setTenantPk( tenantEntity.getTenantPk() );
        tenant.setId( tenantEntity.getId() );
        tenant.setUseYn( tenantEntity.isUseYn() );
        tenant.setDefaultYn( tenantEntity.isDefaultYn() );
        tenant.setVisibleYn( tenantEntity.isVisibleYn() );
        tenant.setTitle( tenantEntity.getTitle() );
        tenant.setLabel( tenantEntity.getLabel() );
        tenant.setUrl( tenantEntity.getUrl() );
        tenant.setCreatedBy( tenantEntity.getCreatedBy() );
        tenant.setCreatedDate( tenantEntity.getCreatedDate() );
        tenant.setUpdatedBy( tenantEntity.getUpdatedBy() );
        tenant.setUpdatedDate( tenantEntity.getUpdatedDate() );

        return tenant;
    }
}
