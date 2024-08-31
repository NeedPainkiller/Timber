package xyz.needpainkiller.api.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.needpainkiller.api.authentication.AuthorizationService;
import xyz.needpainkiller.api.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.user_hex.adapter.out.persistence.repository.RoleRepo;
import xyz.needpainkiller.api.user.dao.RoleSpecification;
import xyz.needpainkiller.api.user.dao.UserRoleMapRepo;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.RoleRequests;
import xyz.needpainkiller.api.user_hex.domain.error.RoleException;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.api.user_hex.domain.model.UserRoleMap;
import xyz.needpainkiller.common.dto.SearchCollectionResult;
import xyz.needpainkiller.helper.TimeHelper;
import xyz.needpainkiller.helper.ValidationHelper;

import java.util.List;

import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.TENANT_CONFLICT;
import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.*;

@Slf4j
@Service
public class RoleService {
    public static Long SUPER_ADMIN = 1L;

    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private UserRoleMapRepo userRoleMapRepo;


    public boolean isRoleExist(Long rolePk) {
        return roleRepo.findAll().stream().filter(Role::isAvailable).map(Role::getId).anyMatch(integer -> integer.equals(rolePk));
    }


    public boolean isRoleExist(Long tenantPk, String roleNm) {
        return roleRepo.findAll().stream()
                .filter(role -> role.filterByTenant(tenantPk))
                .filter(Role::isAvailable)
                .map(Role::getRoleName)
                .anyMatch(name -> name.equals(roleNm));
    }


    @Cacheable(value = "RoleList", key = "'selectAll'")
    public List<Role> selectAll() {
        return roleRepo.findAll().stream().filter(Role::isAvailable).toList();
    }


    @Cacheable(value = "Role", key = "'selectRoleByRolePk-' + #p0")
    public Role selectRoleByRolePk(Long rolePk) {
        return roleRepo.findById(rolePk).orElseThrow(() -> new RoleException(ROLE_NOT_EXIST));
    }


    @Cacheable(value = "Role", key = "'selectRoleByRoleNm-' + #p0")
    public Role selectRoleByRoleNm(String roleNm) {
        return roleRepo.findAll().stream().filter(Role::isAvailable).filter(role -> role.getRoleName().equals(roleNm.trim())).findAny().orElseThrow(() -> new RoleException(ROLE_NOT_EXIST));
    }


    public SearchCollectionResult<Role> selectRoleList(RoleRequests.SearchRoleRequest param) {
        Specification<Role> specification = Specification.where(RoleSpecification.search(param));
        Page<Role> rolePage = roleRepo.findAll(specification, param.pageOf());
        List<Role> roleList = rolePage.getContent();
        long total = rolePage.getTotalElements();
        return SearchCollectionResult.<Role>builder().collection(roleList).foundRows(total).build();
    }


    public List<UserRoleMap> selectUserRoleMap() {
        return userRoleMapRepo.findAll();
    }


    public List<UserRoleMap> selectUserRoleMap(List<Long> userPkList) {
        userPkList = userPkList.stream().distinct().toList();
        return userRoleMapRepo.findByUserPkIn(userPkList);
    }


    @Cacheable(value = "UserRole", key = "'selectUserPkListByRolePk-' + #p0")
    public List<Long> selectUserPkListByRolePk(Long rolePk) {
        return userRoleMapRepo.findByRolePk(rolePk).stream().map(UserRoleMap::getUserPk).toList();
    }


    @Cacheable(value = "UserRole", key = "'selectUserPkListByRolePkList-' + #p0.hashCode()")
    public List<Long> selectUserPkListByRolePkList(List<Long> rolePkList) {
        rolePkList = rolePkList.stream().distinct().toList();
        return userRoleMapRepo.findByRolePkIn(rolePkList).stream().map(UserRoleMap::getUserPk).toList();
    }


    @Cacheable(value = "RoleList", key = "'selectRolesByUser-' + #p0.getId()")
    public List<Role> selectRolesByUser(User user) {
        return selectRolesByUser(user.getId());
    }


    @Cacheable(value = "RoleList", key = "'selectRolesByUser-' + #p0")
    public List<Role> selectRolesByUser(Long userPk) {
        List<Long> rolePkList = userRoleMapRepo.findByUserPk(userPk).stream().map(UserRoleMap::getRolePk).distinct().toList();
        return roleRepo.findByIdIn(rolePkList);
    }


    @Cacheable(value = "RoleList", key = "'selectRolesByPkList-' + #p0.hashCode()")
    public List<Role> selectRolesByPkList(List<Long> rolePkList) {
        List<Role> roleList = roleRepo.findAll().stream().filter(Role::isAvailable).filter(role -> rolePkList.contains(role.getId())).toList();
        if (roleList.isEmpty()) {
            throw new RoleException(ROLE_NOT_EXIST);
        }
        return roleList;
    }


    @Cacheable(value = "RoleList", key = "'selectRolesByNameList-' + #p0.hashCode()")
    public List<Role> selectRolesByNameList(List<String> roleNmList) {
        return roleRepo.findAll().stream().filter(Role::isAvailable).filter(role -> roleNmList.contains(role.getRoleName())).toList();
    }


    @Cacheable(value = "RoleAuthorityList", key = "'selectAuthorityByUser-' + #p0.getId()")
    public List<String> selectAuthorityByUser(User user) {
        return selectAuthorityByUser(user.getId());
    }


    @Cacheable(value = "RoleAuthorityList", key = "'selectAuthorityByUser-' + #p0")
    public List<String> selectAuthorityByUser(Long userPk) {
        List<Role> roles = selectRolesByUser(userPk);
        return roles.stream().map(Role::getAuthority).toList();
    }


    public boolean hasSystemAdminRole(List<Role> roleList) {
        return roleList.stream().anyMatch(Role::isSystemAdmin);
    }


    public boolean hasAdminRole(List<Role> roleList) {
        return roleList.stream().anyMatch(Role::isAdmin);
    }


    public boolean hasEditableRole(List<Role> roleList) {
        return roleList.stream().anyMatch(Role::isEditable);
    }


    public boolean isEditableAuthority(User user, Long ownerUserPk) {
        List<Role> userRoles = selectRolesByUser(user);
        return isEditableAuthority(user, userRoles, ownerUserPk);
    }


    public boolean isEditableAuthority(User user, List<Role> userRoles, Long ownerUserPk) {
        if (!hasAdminRole(userRoles)) { //관리자 권한 아님
            if (ownerUserPk != null && ownerUserPk != 0) {
                return user.getId().equals(ownerUserPk); // 본인 맞음
            }
        }
        return true; // 관리자 권한임
    }


    public void checkRequestRoleAuthority(List<Role> requestRoleList, List<Role> authority) {
        if (!hasAdminRole(authority) && hasAdminRole(requestRoleList)) {// Admin 권한이 없으나, Admin 을 추가하려는 경우
            throw new RoleException(ROLE_CAN_NOT_MANAGE_ADMIN);
        }
    }


    @Transactional
    @Caching(evict = {@CacheEvict(value = "UserRole", allEntries = true), @CacheEvict(value = "Role", allEntries = true), @CacheEvict(value = "RoleList", allEntries = true), @CacheEvict(value = "RoleAuthorityList", allEntries = true)})
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
    public void deleteUserRole(Long userPk) {
        List<UserRoleMap> userRoleMapList = userRoleMapRepo.findByUserPk(userPk);
        userRoleMapRepo.deleteAll(userRoleMapList);
    }
}
