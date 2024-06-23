package xyz.needpainkiller.api.user_hex.domain.event;

import lombok.*;
import xyz.needpainkiller.api.user_hex.domain.model.User;
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
