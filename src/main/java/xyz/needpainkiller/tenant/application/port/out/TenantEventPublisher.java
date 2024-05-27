package xyz.needpainkiller.tenant.application.port.out;

import xyz.needpainkiller.tenant.domain.event.TenantEvent;
import xyz.needpainkiller.tenant.domain.model.Tenant;

public interface TenantEventPublisher {

    void publishCreateEvent(Tenant tenant);
    void publishUpdateEvent(Tenant tenant);
    void publishDeleteEvent(Tenant tenant);
}
