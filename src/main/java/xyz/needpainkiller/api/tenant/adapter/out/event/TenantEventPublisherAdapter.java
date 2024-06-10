package xyz.needpainkiller.api.tenant.adapter.out.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import xyz.needpainkiller.lib.event.EventType;
import xyz.needpainkiller.api.tenant.application.port.out.TenantEventPublisher;
import xyz.needpainkiller.api.tenant.domain.event.TenantEvent;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;

@RequiredArgsConstructor
public class TenantEventPublisherAdapter implements TenantEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishCreateEvent(Tenant tenant) {
        TenantEvent event = new TenantEvent(EventType.CREATE, tenant);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishUpdateEvent(Tenant tenant) {
        TenantEvent event = new TenantEvent(EventType.UPDATE, tenant);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishDeleteEvent(Tenant tenant) {
        TenantEvent event = new TenantEvent(EventType.DELETE, tenant);
        applicationEventPublisher.publishEvent(event);
    }
}
