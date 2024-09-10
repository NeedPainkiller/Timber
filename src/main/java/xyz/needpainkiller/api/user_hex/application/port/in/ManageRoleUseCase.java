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
    @Caching(evict = {@CacheEvict(value = "UserRole", allEntries = true), @CacheEvict(value = "Role", allEntries = true), @CacheEvict(value = "RoleList", allEntries = true), @CacheEvict(value = "RoleAuthorityList", allEntries = true)})
    Role createRole(RoleRequests.UpsertRoleRequest param, User requester);

    @Transactional
    @Caching(evict = {@CacheEvict(value = "UserRole", allEntries = true), @CacheEvict(value = "Role", allEntries = true), @CacheEvict(value = "RoleList", allEntries = true), @CacheEvict(value = "RoleAuthorityList", allEntries = true)})
    Role updateRole(Long rolePk, RoleRequests.UpsertRoleRequest param, User requester);

    @Transactional
    @Caching(evict = {@CacheEvict(value = "UserRole", allEntries = true), @CacheEvict(value = "Role", allEntries = true), @CacheEvict(value = "RoleList", allEntries = true), @CacheEvict(value = "RoleAuthorityList", allEntries = true)})
    void deleteRole(Long tenantPk, Long rolePk, User requester);

    @Transactional
    @Caching(evict = {@CacheEvict(value = "UserRole", allEntries = true), @CacheEvict(value = "Role", allEntries = true), @CacheEvict(value = "RoleList", allEntries = true), @CacheEvict(value = "RoleAuthorityList", allEntries = true)})
    void upsertUserRole(Long userPk, List<Role> roleList);

    @Transactional
    @Caching(evict = {@CacheEvict(value = "UserRole", allEntries = true), @CacheEvict(value = "Role", allEntries = true), @CacheEvict(value = "RoleList", allEntries = true), @CacheEvict(value = "RoleAuthorityList", allEntries = true)})
    void deleteUserRole(Long userPk);
}
