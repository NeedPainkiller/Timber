package xyz.needpainkiller.api.tenant;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.needpainkiller.api.tenant.adapter.out.event.TenantEventPublisherAdapter;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.TenantPersistenceAdapter;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.mapper.TenantPersistenceMapper;
import xyz.needpainkiller.api.tenant.adapter.out.persistence.repository.TenantRepository;
import xyz.needpainkiller.api.tenant.domain.service.FindTenantService;
import xyz.needpainkiller.api.tenant.domain.service.ManageTenantService;

@Configuration
public class TenantBeanConfiguration {

    @Bean
    public TenantPersistenceAdapter tenantPersistenceAdapter(TenantRepository tenantRepository, TenantPersistenceMapper tenantPersistenceMapper) {
        return new TenantPersistenceAdapter(tenantRepository, tenantPersistenceMapper);
    }

    @Bean
    public TenantEventPublisherAdapter tenantEventPublisherAdapter(ApplicationEventPublisher applicationEventPublisher) {
        return new TenantEventPublisherAdapter(applicationEventPublisher);
    }

    @Bean
    public FindTenantService findTenantService(TenantPersistenceAdapter tenantPersistenceAdapter) {
        return new FindTenantService(tenantPersistenceAdapter);
    }

    @Bean
    public ManageTenantService manageTenantService(TenantPersistenceAdapter tenantPersistenceAdapter, TenantEventPublisherAdapter tenantEventPublisherAdapter) {
        return new ManageTenantService(tenantPersistenceAdapter, tenantEventPublisherAdapter);
    }
}
