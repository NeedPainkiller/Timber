package xyz.needpainkiller.api.tenant.adapter.in.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import xyz.needpainkiller.api.tenant.domain.event.TenantEvent;

@Slf4j
@Component
public class TenantEventListenerAdapter {

    @EventListener
    public void handle(TenantEvent event) {
        log.info("Tenant event received: " + event);
    }
}
