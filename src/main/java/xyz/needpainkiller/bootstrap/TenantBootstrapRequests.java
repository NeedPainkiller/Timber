package xyz.needpainkiller.bootstrap;

import lombok.*;
import xyz.needpainkiller.api.user.adapter.in.web.data.RoleRequests;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantBootstrapRequests {

    @Getter
    @Setter
    @AllArgsConstructor
    public static final class CreateTenantRoleRequest extends RoleRequests.UpsertRoleRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = -1220430213810340303L;
        private Long tenantPk;
        private final String name = "ADMIN";
        private final String description = "관리자 권한";
        private final Boolean isAdmin = true;
        private final List<Long> apiList = new ArrayList<>();

        public CreateTenantRoleRequest() {
            super();
            apiList.add(10101L);
            apiList.add(10102L);
            apiList.add(10201L);
            apiList.add(10301L);
            apiList.add(20101L);
            apiList.add(20102L);
            apiList.add(20103L);
            apiList.add(20104L);
            apiList.add(20105L);
            apiList.add(20201L);
            apiList.add(20202L);
            apiList.add(20203L);
            apiList.add(20301L);
            apiList.add(20302L);
            apiList.add(20303L);
            apiList.add(20305L);
            apiList.add(20306L);
            apiList.add(20307L);
            apiList.add(20311L);
            apiList.add(20312L);
            apiList.add(20313L);
            apiList.add(20314L);
            apiList.add(20315L);
            apiList.add(30201L);
            apiList.add(30202L);
            apiList.add(30301L);
            apiList.add(30302L);
            apiList.add(40101L);
            apiList.add(40102L);
            apiList.add(40103L);
            apiList.add(40104L);
            apiList.add(40201L);
            apiList.add(40202L);
            apiList.add(40203L);
            apiList.add(40204L);
            apiList.add(40205L);
            apiList.add(40206L);
            apiList.add(40207L);
            apiList.add(40208L);
            apiList.add(40211L);
            apiList.add(40301L);
            apiList.add(40302L);
            apiList.add(40303L);
            apiList.add(40304L);
            apiList.add(40305L);
            apiList.add(40401L);
            apiList.add(40402L);
            apiList.add(40403L);
            apiList.add(40404L);
            apiList.add(40405L);
            apiList.add(40406L);
            apiList.add(40407L);
            apiList.add(40408L);
            apiList.add(50101L);
            apiList.add(50102L);
            apiList.add(50201L);
            apiList.add(60101L);
            apiList.add(60102L);
            apiList.add(60103L);
            apiList.add(60104L);
            apiList.add(60105L);
            apiList.add(60106L);
            apiList.add(60107L);
            apiList.add(60108L);
            apiList.add(60109L);
            apiList.add(60110L);
            apiList.add(60111L);
            apiList.add(60201L);
            apiList.add(60202L);
            apiList.add(60203L);
            apiList.add(60204L);
            apiList.add(60205L);
            apiList.add(60206L);
            apiList.add(60207L);
            apiList.add(60208L);
            apiList.add(60209L);
            apiList.add(60210L);
            apiList.add(60211L);
            apiList.add(60301L);
            apiList.add(60302L);
            apiList.add(60303L);
            apiList.add(60304L);
            apiList.add(60305L);
            apiList.add(60306L);
            apiList.add(60307L);
            apiList.add(60308L);
            apiList.add(60309L);
            apiList.add(60310L);
            apiList.add(60311L);
            apiList.add(60401L);
            apiList.add(60402L);
            apiList.add(60403L);
            apiList.add(60404L);
            apiList.add(60405L);
            apiList.add(60406L);
            apiList.add(60407L);
            apiList.add(60408L);
            apiList.add(60409L);
            apiList.add(60410L);
            apiList.add(60411L);
            apiList.add(70101L);
            apiList.add(70102L);
            apiList.add(70104L);
            apiList.add(70105L);
            apiList.add(70106L);
            apiList.add(70107L);
            apiList.add(70201L);
            apiList.add(70203L);
            apiList.add(70204L);
            apiList.add(70206L);
            apiList.add(70207L);
            apiList.add(70208L);
            apiList.add(70301L);
            apiList.add(70302L);
            apiList.add(70303L);
            apiList.add(70304L);
            apiList.add(70305L);
            apiList.add(70401L);
            apiList.add(70402L);
            apiList.add(70403L);
            apiList.add(70404L);
            apiList.add(70405L);
            apiList.add(70406L);
            apiList.add(70501L);
            apiList.add(70502L);
            apiList.add(70503L);
            apiList.add(70504L);
            apiList.add(70505L);
            apiList.add(70601L);
            apiList.add(70602L);
            apiList.add(70703L);
            apiList.add(70902L);
            apiList.add(80101L);
            apiList.add(80102L);
            apiList.add(80103L);
            apiList.add(80104L);
            apiList.add(80105L);
            apiList.add(80106L);
            apiList.add(80107L);
            apiList.add(80108L);
            apiList.add(80109L);
            apiList.add(80110L);
            apiList.add(80111L);
            apiList.add(80201L);
            apiList.add(80202L);
            apiList.add(80203L);
            apiList.add(80204L);
        }
    }

}