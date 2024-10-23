package xyz.needpainkiller.api.user.application.port.out;

import xyz.needpainkiller.api.user.domain.model.Role;

public interface RoleEventPublisher {

    void publishCreateEvent(Role role);
    void publishUpdateEvent(Role role);
    void publishDeleteEvent(Role role);
}
