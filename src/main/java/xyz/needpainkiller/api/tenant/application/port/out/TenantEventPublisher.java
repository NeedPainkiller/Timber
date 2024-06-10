package xyz.needpainkiller.api.tenant.application.port.out;

import xyz.needpainkiller.api.tenant.domain.model.Tenant;

public interface TenantEventPublisher {

    void publishCreateEvent(Tenant tenant);
    void publishUpdateEvent(Tenant tenant);
    void publishDeleteEvent(Tenant tenant);
}
