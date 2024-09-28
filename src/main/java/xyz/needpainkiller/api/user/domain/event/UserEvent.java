package xyz.needpainkiller.api.user.domain.event;

import lombok.*;
import xyz.needpainkiller.api.user.domain.model.User;
import xyz.needpainkiller.lib.event.EventType;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    private EventType eventType;

    private User user;
}
