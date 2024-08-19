package xyz.needpainkiller.api.user_hex.application.port.out;

import xyz.needpainkiller.api.user_hex.domain.model.User;

public interface UserEventPublisher {

    void publishCreateEvent(User user);
    void publishUpdateEvent(User user);
    void publishDeleteEvent(User user);
}
