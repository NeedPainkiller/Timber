package xyz.needpainkiller.api.user.application.port.out;

import xyz.needpainkiller.api.user.domain.model.User;

public interface UserEventPublisher {

    void publishCreateEvent(User user);
    void publishUpdateEvent(User user);
    void publishDeleteEvent(User user);
}
