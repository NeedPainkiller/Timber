package xyz.needpainkiller.api.tenant.domain.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import reactor.core.publisher.Mono;
import xyz.needpainkiller.api.tenant.application.port.in.FindTenantUseCase;
import xyz.needpainkiller.api.tenant.application.port.out.TenantOutputPort;
import xyz.needpainkiller.api.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.TENANT_NOT_EXIST;


@Slf4j
@AllArgsConstructor
public class FindTenantService implements FindTenantUseCase {

    private final TenantOutputPort tenantOutputPort;

    @Override
    @Cacheable(value = "TenantList", key = "'selectTenantList'")
    public List<Tenant> selectTenantList() {
        return tenantOutputPort.selectTenantList().stream().filter(Tenant.predicateAvailableTenant).toList();
    }

    @Override
    @Cacheable(value = "TenantList", key = "'selectPublicTenantList'")
    public List<Tenant> selectPublicTenantList() {
        return tenantOutputPort.selectTenantList().stream().filter(Tenant.predicateAvailableTenant).filter(Tenant.predicatePublicTenant).toList();
    }

    @Override
    public Map<Long, Tenant> selectTenantMap() {
        return tenantOutputPort.selectTenantList().stream().filter(Tenant.predicateAvailableTenant).collect(Collectors.toMap(Tenant::getId, t -> t));
    }

    @Override
    public List<Long> selectTenantPkList() {
        return tenantOutputPort.selectTenantList().stream().filter(Tenant.predicateAvailableTenant).map(Tenant::getId).toList();
    }

    @Override
    @Cacheable(value = "Tenant", key = "'selectTenant' + #p0", unless = "#result == null")
    public Tenant selectTenant(Long tenantPk) {
        return tenantOutputPort.selectTenant(tenantPk).filter(Tenant.predicateAvailableTenant)
                .orElseThrow(() -> new TenantException(TENANT_NOT_EXIST));
    }

    @Override
    @Cacheable(value = "Tenant", key = "'selectDefatultTenant'", unless = "#result == null")
    public Tenant selectDefatultTenant() {
        return tenantOutputPort.selectTenantList().stream().filter(Tenant.predicateDefaultTenant).findFirst().orElseThrow(() -> new TenantException(TENANT_NOT_EXIST));
    }

    @Override
    public Mono<Tenant> selectDefatultTenantMono() {
        return Mono.fromCallable(() -> tenantOutputPort.selectTenantList().stream().filter(Tenant.predicateDefaultTenant).findFirst())
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty));
    }
}

