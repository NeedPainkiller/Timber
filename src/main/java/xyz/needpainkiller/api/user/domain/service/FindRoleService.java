package xyz.needpainkiller.api.user.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import xyz.needpainkiller.api.authentication.AuthorizationService;
import xyz.needpainkiller.api.user.adapter.in.web.data.RoleRequests;
import xyz.needpainkiller.api.user.adapter.out.persistence.repository.RoleRepository;
import xyz.needpainkiller.api.user.adapter.out.persistence.repository.RoleSpecification;
import xyz.needpainkiller.api.user.adapter.out.persistence.repository.UserRoleMapRepo;
import xyz.needpainkiller.api.user.application.port.in.FindRoleUseCase;
import xyz.needpainkiller.api.user.domain.error.RoleException;
import xyz.needpainkiller.api.user.domain.model.Role;
import xyz.needpainkiller.api.user.domain.model.User;
import xyz.needpainkiller.api.user.domain.model.UserRoleMap;
import xyz.needpainkiller.common.dto.SearchCollectionResult;

import java.util.List;

import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.*;

@Slf4j
@Service
public class FindRoleService implements FindRoleUseCase {
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleMapRepo userRoleMapRepo;

    @Override
    public boolean isRoleExist(Long rolePk) {
        return roleRepository.findAll().stream().filter(Role::isAvailable).map(Role::getId).anyMatch(integer -> integer.equals(rolePk));
    }


    @Override
    public boolean isRoleExist(Long tenantPk, String roleNm) {
        return roleRepository.findAll().stream()
                .filter(role -> role.filterByTenant(tenantPk))
                .filter(Role::isAvailable)
                .map(Role::getRoleName)
                .anyMatch(name -> name.equals(roleNm));
    }


    @Cacheable(value = "RoleList", key = "'selectAll'")
    @Override
    public List<Role> selectAll() {
        return roleRepository.findAll().stream().filter(Role::isAvailable).toList();
    }


    @Cacheable(value = "Role", key = "'selectRoleByRolePk-' + #p0")
    @Override
    public Role selectRoleByRolePk(Long rolePk) {
        return roleRepository.findById(rolePk).orElseThrow(() -> new RoleException(ROLE_NOT_EXIST));
    }


    @Cacheable(value = "Role", key = "'selectRoleByRoleNm-' + #p0")
    @Override
    public Role selectRoleByRoleNm(String roleNm) {
        return roleRepository.findAll().stream().filter(Role::isAvailable).filter(role -> role.getRoleName().equals(roleNm.trim())).findAny().orElseThrow(() -> new RoleException(ROLE_NOT_EXIST));
    }


    @Override
    public SearchCollectionResult<Role> selectRoleList(RoleRequests.SearchRoleRequest param) {
        Specification<Role> specification = Specification.where(RoleSpecification.search(param));
        Page<Role> rolePage = roleRepository.findAll(specification, param.pageOf());
        List<Role> roleList = rolePage.getContent();
        long total = rolePage.getTotalElements();
        return SearchCollectionResult.<Role>builder().collection(roleList).foundRows(total).build();
    }


    @Override
    public List<UserRoleMap> selectUserRoleMap() {
        return userRoleMapRepo.findAll();
    }


    @Override
    public List<UserRoleMap> selectUserRoleMap(List<Long> userPkList) {
        userPkList = userPkList.stream().distinct().toList();
        return userRoleMapRepo.findByUserPkIn(userPkList);
    }


    @Cacheable(value = "UserRole", key = "'selectUserPkListByRolePk-' + #p0")
    @Override
    public List<Long> selectUserPkListByRolePk(Long rolePk) {
        return userRoleMapRepo.findByRolePk(rolePk).stream().map(UserRoleMap::getUserPk).toList();
    }


    @Cacheable(value = "UserRole", key = "'selectUserPkListByRolePkList-' + #p0.hashCode()")
    @Override
    public List<Long> selectUserPkListByRolePkList(List<Long> rolePkList) {
        rolePkList = rolePkList.stream().distinct().toList();
        return userRoleMapRepo.findByRolePkIn(rolePkList).stream().map(UserRoleMap::getUserPk).toList();
    }


    @Cacheable(value = "RoleList", key = "'selectRolesByUser-' + #p0.getId()")
    @Override
    public List<Role> selectRolesByUser(User user) {
        return selectRolesByUser(user.getId());
    }


    @Cacheable(value = "RoleList", key = "'selectRolesByUser-' + #p0")
    @Override
    public List<Role> selectRolesByUser(Long userPk) {
        List<Long> rolePkList = userRoleMapRepo.findByUserPk(userPk).stream().map(UserRoleMap::getRolePk).distinct().toList();
        return roleRepository.findByIdIn(rolePkList);
    }


    @Cacheable(value = "RoleList", key = "'selectRolesByPkList-' + #p0.hashCode()")
    @Override
    public List<Role> selectRolesByPkList(List<Long> rolePkList) {
        List<Role> roleList = roleRepository.findAll().stream().filter(Role::isAvailable).filter(role -> rolePkList.contains(role.getId())).toList();
        if (roleList.isEmpty()) {
            throw new RoleException(ROLE_NOT_EXIST);
        }
        return roleList;
    }


    @Cacheable(value = "RoleList", key = "'selectRolesByNameList-' + #p0.hashCode()")
    @Override
    public List<Role> selectRolesByNameList(List<String> roleNmList) {
        return roleRepository.findAll().stream().filter(Role::isAvailable).filter(role -> roleNmList.contains(role.getRoleName())).toList();
    }


    @Cacheable(value = "RoleAuthorityList", key = "'selectAuthorityByUser-' + #p0.getId()")
    @Override
    public List<String> selectAuthorityByUser(User user) {
        return selectAuthorityByUser(user.getId());
    }


    @Cacheable(value = "RoleAuthorityList", key = "'selectAuthorityByUser-' + #p0")
    @Override
    public List<String> selectAuthorityByUser(Long userPk) {
        List<Role> roles = selectRolesByUser(userPk);
        return roles.stream().map(Role::getAuthority).toList();
    }


    @Override
    public boolean hasSystemAdminRole(List<Role> roleList) {
        return roleList.stream().anyMatch(Role::isSystemAdmin);
    }


    @Override
    public boolean hasAdminRole(List<Role> roleList) {
        return roleList.stream().anyMatch(Role::isAdmin);
    }


    @Override
    public boolean hasEditableRole(List<Role> roleList) {
        return roleList.stream().anyMatch(Role::isEditable);
    }


    @Override
    public boolean isEditableAuthority(User user, Long ownerUserPk) {
        List<Role> userRoles = selectRolesByUser(user);
        return isEditableAuthority(user, userRoles, ownerUserPk);
    }


    @Override
    public boolean isEditableAuthority(User user, List<Role> userRoles, Long ownerUserPk) {
        if (!hasAdminRole(userRoles)) { //관리자 권한 아님
            if (ownerUserPk != null && ownerUserPk != 0) {
                return user.getId().equals(ownerUserPk); // 본인 맞음
            }
        }
        return true; // 관리자 권한임
    }


    @Override
    public void checkRequestRoleAuthority(List<Role> requestRoleList, List<Role> authority) {
        if (!hasAdminRole(authority) && hasAdminRole(requestRoleList)) {// Admin 권한이 없으나, Admin 을 추가하려는 경우
            throw new RoleException(ROLE_CAN_NOT_MANAGE_ADMIN);
        }
    }
}
