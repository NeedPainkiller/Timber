package xyz.needpainkiller.api.user_hex.application.port.in;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.transaction.annotation.Transactional;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.RoleRequests;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;

import java.util.List;

public interface ManageRoleUseCase {
    @Transactional
    Role createRole(RoleRequests.UpsertRoleRequest param, User requester);

    @Transactional
    Role updateRole(Long rolePk, RoleRequests.UpsertRoleRequest param, User requester);

    @Transactional
    void deleteRole(Long tenantPk, Long rolePk, User requester);

    @Transactional
    void upsertUserRole(Long userPk, List<Role> roleList);

    @Transactional
    void deleteUserRole(Long userPk);
}
