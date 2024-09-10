package xyz.needpainkiller.api.user_hex.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.needpainkiller.api.authentication.AuthorizationService;
import xyz.needpainkiller.api.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.RoleRequests;
import xyz.needpainkiller.api.user_hex.adapter.out.persistence.repository.RoleRepo;
import xyz.needpainkiller.api.user_hex.adapter.out.persistence.repository.UserRoleMapRepo;
import xyz.needpainkiller.api.user_hex.application.port.in.ManageRoleUseCase;
import xyz.needpainkiller.api.user_hex.domain.error.RoleException;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.api.user_hex.domain.model.UserRoleMap;
import xyz.needpainkiller.helper.TimeHelper;
import xyz.needpainkiller.helper.ValidationHelper;

import java.util.List;

import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.TENANT_CONFLICT;
import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.*;

@Slf4j
@Service
public class ManageRoleService implements ManageRoleUseCase {
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private UserRoleMapRepo userRoleMapRepo;

    @Transactional
    @Caching(evict = {@CacheEvict(value = "UserRole", allEntries = true), @CacheEvict(value = "Role", allEntries = true), @CacheEvict(value = "RoleList", allEntries = true), @CacheEvict(value = "RoleAuthorityList", allEntries = true)})
    @Override
    public Role createRole(RoleRequests.UpsertRoleRequest param, User requester) {
        String roleName = param.getName();

        Long requesterPk = requester.getId();
        Long tenantPk = param.getTenantPk();

        ValidationHelper.checkAnyRequiredEmpty(roleName);
        if (isRoleExist(tenantPk, roleName)) {
            throw new RoleException(ROLE_ALREADY_EXIST);
        }

        Role role = new Role();
        role.setTenantPk(tenantPk);
        role.setUseYn(true);
        role.setRoleName(roleName);
        role.setRoleDescription(param.getDescription());
        role.setSystemAdmin(false);
        role.setAdmin(param.getIsAdmin());
        role.setEditable(true);
        role.setCreatedBy(requesterPk);
        role.setUpdatedBy(requesterPk);
        role = roleRepo.save(role);
        authorizationService.upsertApiRole(role, param.getApiList());
        return role;
    }


    @Transactional
    @Caching(evict = {@CacheEvict(value = "UserRole", allEntries = true), @CacheEvict(value = "Role", allEntries = true), @CacheEvict(value = "RoleList", allEntries = true), @CacheEvict(value = "RoleAuthorityList", allEntries = true)})
    @Override
    public Role updateRole(Long rolePk, RoleRequests.UpsertRoleRequest param, User requester) {
        Role role = selectRoleByRolePk(rolePk);

        Long requesterPk = requester.getId();
        Long tenantPk = param.getTenantPk();

        if (!role.isUseYn()) {
            throw new RoleException(ROLE_DELETED);
        }
        if (!role.isEditable()) {
            throw new RoleException(ROLE_CAN_NOT_EDITABLE);
        }
        if (!role.getTenantPk().equals(tenantPk)) {
            throw new TenantException(TENANT_CONFLICT);
        }

        role.setTenantPk(tenantPk);
        role.setUseYn(true);
        role.setRoleName(param.getName());
        role.setRoleDescription(param.getDescription());
        role.setSystemAdmin(false);
        role.setAdmin(param.getIsAdmin());
        role.setEditable(true);
        role.setUpdatedBy(requesterPk);
        role = roleRepo.save(role);
        authorizationService.upsertApiRole(role, param.getApiList());
        return role;
    }


    @Transactional
    @Caching(evict = {@CacheEvict(value = "UserRole", allEntries = true), @CacheEvict(value = "Role", allEntries = true), @CacheEvict(value = "RoleList", allEntries = true), @CacheEvict(value = "RoleAuthorityList", allEntries = true)})
    @Override
    public void deleteRole(Long tenantPk, Long rolePk, User requester) {
        Long requesterPk = requester.getId();
        Role role = selectRoleByRolePk(rolePk);
        if (!role.isUseYn()) {
            throw new RoleException(ROLE_DELETED);
        }
        if (!role.isEditable()) {
            throw new RoleException(ROLE_CAN_NOT_EDITABLE);
        }
        if (!role.getTenantPk().equals(tenantPk)) {
            throw new TenantException(TENANT_CONFLICT);
        }
        role.setUseYn(false);
        role.setUpdatedBy(requesterPk);
        role.setUpdatedDate(TimeHelper.now());
        roleRepo.save(role);
    }


    @Transactional
    @Caching(evict = {@CacheEvict(value = "UserRole", allEntries = true), @CacheEvict(value = "Role", allEntries = true), @CacheEvict(value = "RoleList", allEntries = true), @CacheEvict(value = "RoleAuthorityList", allEntries = true)})
    @Override
    public void upsertUserRole(Long userPk, List<Role> roleList) {
        this.deleteUserRole(userPk);
        if (roleList == null) return;
        roleList.forEach(role -> {
            UserRoleMap userRoleMap = new UserRoleMap();
            userRoleMap.setUserPk(userPk);
            userRoleMap.setRolePk(role.getId());
            userRoleMapRepo.save(userRoleMap);
        });
    }


    @Transactional
    @Caching(evict = {@CacheEvict(value = "UserRole", allEntries = true), @CacheEvict(value = "Role", allEntries = true), @CacheEvict(value = "RoleList", allEntries = true), @CacheEvict(value = "RoleAuthorityList", allEntries = true)})
    @Override
    public void deleteUserRole(Long userPk) {
        List<UserRoleMap> userRoleMapList = userRoleMapRepo.findByUserPk(userPk);
        userRoleMapRepo.deleteAll(userRoleMapList);
    }
}
