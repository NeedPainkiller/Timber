package xyz.needpainkiller.api.user_hex.adapter.in.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import xyz.needpainkiller.api.tenant.domain.event.TenantEvent;
import xyz.needpainkiller.api.user_hex.domain.event.UserEvent;

@Slf4j
@Component
public class UserEventListenerAdapter {

    @EventListener
    public void handle(UserEvent event) {
        log.info("User event received: " + event);
    }
}
