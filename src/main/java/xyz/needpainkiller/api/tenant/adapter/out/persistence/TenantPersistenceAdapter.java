package xyz.needpainkiller.api.tenant.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.entity.TenantEntity;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.mapper.TenantPersistenceMapper;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.repository.TenantRepository;
import xyz.needpainkiller.api.tenant.application.port.out.TenantOutputPort;
import xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode;
import xyz.needpainkiller.api.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.TENANT_DEFAULT_CAN_NOT_DELETE;
import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.TENANT_DELETED;

@RequiredArgsConstructor
public class TenantPersistenceAdapter implements TenantOutputPort {

    /**
     * 테넌트 Repository
     */
    private final TenantRepository tenantRepository;

    /**
     * 테넌트 Mapper ( Persistence -> Domain / Domain -> Persistence )
     */
    private final TenantPersistenceMapper tenantPersistenceMapper;

    @Override
    public Optional<Tenant> selectTenant(Long tenantPk) {
        return tenantRepository.findById(tenantPk).map(tenantPersistenceMapper::toTenant);
    }

    @Override
    public Optional<Tenant> selectTenantAvailable(Long tenantPk) {
        return tenantRepository.findById(tenantPk).filter(TenantEntity::isUseYn).map(tenantPersistenceMapper::toTenant);
    }

    @Override
    public List<Tenant> selectTenantList() {
        return tenantRepository.findAll().stream().map(tenantPersistenceMapper::toTenant).toList();
    }

    @Override
    public List<Long> selectTenantPkList() {
        return tenantRepository.findAll().stream().map(TenantEntity::getId).toList();
    }

    @Override
    public Map<Long, Tenant> selectTenantMap() {
        return tenantRepository.findAll().stream().collect(Collectors.toMap(TenantEntity::getId, tenantPersistenceMapper::toTenant));
    }

    @Override
    public Tenant create(Tenant tenant) throws TenantException {
        TenantEntity tenantEntity = tenantPersistenceMapper.toTenantEntity(tenant);
        tenantRepository.save(tenantEntity);
        return tenantPersistenceMapper.toTenant(tenantEntity);
    }

    @Override
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public Tenant update(Tenant tenant) throws TenantException {
        Long tenantPk = tenant.getId();
        Optional<TenantEntity> tenantEntity = tenantRepository.findById(tenantPk);
        if (tenantEntity.isEmpty()) {
            throw new TenantException(TenantErrorCode.TENANT_NOT_EXIST);
        }

        TenantEntity tenantEntityToCheck = tenantEntity.get();
        if (!tenantEntityToCheck.isUseYn()) {
            throw new TenantException(TENANT_DELETED);
        }

        TenantEntity updatedTenantEntity = tenantPersistenceMapper.toTenantEntity(tenant);
        tenantRepository.save(updatedTenantEntity);
        return tenantPersistenceMapper.toTenant(updatedTenantEntity);
    }

    @Override
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public Tenant delete(Tenant tenant) throws TenantException {
        Long tenantPk = tenant.getId();
        Optional<TenantEntity> tenantEntity = tenantRepository.findById(tenantPk);
        if (tenantEntity.isEmpty()) {
            throw new TenantException(TenantErrorCode.TENANT_NOT_EXIST);
        }
        TenantEntity tenantEntityToDelete = tenantEntity.get();
        if (!tenantEntityToDelete.isUseYn()) {
            throw new TenantException(TENANT_DELETED);
        }
        if (tenantEntityToDelete.isDefault()) {
            throw new TenantException(TENANT_DEFAULT_CAN_NOT_DELETE);
        }
        tenantEntityToDelete = tenantRepository.save(tenantEntityToDelete);
        return tenantPersistenceMapper.toTenant(tenantEntityToDelete);
    }

    @Override
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public Tenant delete(Long tenantPk) throws TenantException {
        Optional<TenantEntity> tenantEntity = tenantRepository.findById(tenantPk);
        if (tenantEntity.isEmpty()) {
            throw new TenantException(TenantErrorCode.TENANT_NOT_EXIST);
        }
        TenantEntity tenantEntityToDelete = tenantEntity.get();
        if (!tenantEntityToDelete.isUseYn()) {
            throw new TenantException(TENANT_DELETED);
        }
        if (tenantEntityToDelete.isDefault()) {
            throw new TenantException(TENANT_DEFAULT_CAN_NOT_DELETE);
        }
        tenantEntityToDelete = tenantRepository.save(tenantEntityToDelete);
        return tenantPersistenceMapper.toTenant(tenantEntityToDelete);
    }
}
