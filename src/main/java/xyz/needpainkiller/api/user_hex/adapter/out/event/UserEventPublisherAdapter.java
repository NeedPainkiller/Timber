package xyz.needpainkiller.api.user_hex.adapter.out.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import xyz.needpainkiller.api.user_hex.application.port.out.UserEventPublisher;
import xyz.needpainkiller.api.user_hex.domain.event.UserEvent;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.lib.event.EventType;

@RequiredArgsConstructor
public class UserEventPublisherAdapter implements UserEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishCreateEvent(User user) {
        UserEvent event = new UserEvent(EventType.CREATE, user);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishUpdateEvent(User user) {
        UserEvent event = new UserEvent(EventType.UPDATE, user);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publishDeleteEvent(User user) {
        UserEvent event = new UserEvent(EventType.DELETE, user);
        applicationEventPublisher.publishEvent(event);
    }
}
