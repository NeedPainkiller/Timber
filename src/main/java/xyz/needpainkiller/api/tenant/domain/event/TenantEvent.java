package xyz.needpainkiller.api.tenant.domain.event;

import lombok.*;
import xyz.needpainkiller.lib.event.EventType;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TenantEvent {

    private EventType eventType;

    private Tenant tenant;
}
