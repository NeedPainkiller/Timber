package xyz.needpainkiller.api.user_hex.domain.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import xyz.needpainkiller.api.team.TeamService;
import xyz.needpainkiller.api.team.error.TeamException;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;
import xyz.needpainkiller.api.user.RoleService;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.UserRequests.SearchUserRequest;
import xyz.needpainkiller.api.user_hex.adapter.out.persistence.repository.UserRepo;
import xyz.needpainkiller.api.user_hex.adapter.out.persistence.repository.UserSpecification;
import xyz.needpainkiller.api.user_hex.adapter.out.web.data.UserProfile;
import xyz.needpainkiller.api.user_hex.application.port.in.FindUserUseCase;
import xyz.needpainkiller.api.user_hex.domain.error.UserException;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.api.user_hex.domain.model.UserRoleMap;
import xyz.needpainkiller.common.dto.SearchCollectionResult;

import java.util.*;
import java.util.stream.Collectors;

import static xyz.needpainkiller.api.user_hex.domain.error.UserErrorCode.USER_NOT_EXIST;
import static xyz.needpainkiller.api.user_hex.domain.model.User.SYSTEM_USER;

@Slf4j
@Service
public class FindUserService implements FindUserUseCase {


    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TeamService teamService;

    @PostConstruct
    public void init() {
        log.info("CommonUserService");
        log.info("SYSTEM_USER: {}", SYSTEM_USER);
    }


    @Override
    @Cacheable(value = "User", key = "'selectSystemUser'")
    public User selectSystemUser() {
        return selectUser(SYSTEM_USER);
    }


    @Override
    public Long selectSystemUserPk() {
        return SYSTEM_USER;
    }


    @Override
    public User selectUser(Long userPk) throws UserException {
        User user = userRepo.findUserById(userPk);
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
        return user;
    }


    @Override
    public List<User> selectUserByUserId(String userId) throws UserException {
        userId = userId.trim();
        List<User> userList = userRepo.findUserByUserId(userId);
        if (userList == null || userList.isEmpty()) {
            throw new UserException(USER_NOT_EXIST);
        }
        return userList;
    }


    @Override
    public User selectUserByUserId(Tenant tenant, String userId) throws UserException {
        Long tenantPk = tenant.getId();
        return selectUserByUserId(tenantPk, userId);
    }


    @Override
    public User selectUserByUserId(Long tenantPk, String userId) throws UserException {
        userId = userId.trim();
        List<User> userList = userRepo.findUserByUserId(userId);
        if (userList == null || userList.isEmpty()) {
            throw new UserException(USER_NOT_EXIST);
        }
        return userList.stream().filter(User::isAvailable).filter(user -> user.filterByTenant(tenantPk)).findAny()
                .orElseThrow(() -> new UserException(USER_NOT_EXIST));
    }


    @Override
    public boolean isUserIdExist(Long tenantPk, String userId) {
        userId = userId.trim();
        List<User> userList = userRepo.findUserByUserId(userId);
        if (userList == null || userList.isEmpty()) {
            return false;
        }
        return userList.stream().filter(User::isAvailable).anyMatch(user -> user.getTenantPk().equals(tenantPk));
    }


    @Override
    @Cacheable(value = "UserProfile", key = "'selectUserProfile-' + #p0", unless = "#result == null")
    public UserProfile selectUserProfile(Long userPk) throws UserException {
        User user = userRepo.findUserById(userPk);
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
        return selectUserProfile(user);
    }


    @Override
    @Cacheable(value = "UserProfile", key = "'selectUserProfile-' + #p0.hashCode()", unless = "#result == null")
    public UserProfile selectUserProfile(User user) throws UserException {
        Long userPk = user.getId();
        List<Role> userRoleList = roleService.selectRolesByUser(userPk);
        Long teamPk = user.getTeamPk();
        Team team;
        try {
            team = teamService.selectTeam(teamPk);
        } catch (TeamException e) {
            team = null;
        }
        return new UserProfile(user, team, userRoleList);
    }


    @Override
    @Cacheable(value = "UserList", key = "'selectUserList'")
    public List<User> selectUserList() {
        return userRepo.findAll();
    }


    @Override
    @Cacheable(value = "UserList", key = "'selectUserList-' + #p0")
    public List<User> selectUserList(Long tenantPk) {
        return userRepo.findAll().stream().filter(user -> user.filterByTenant(tenantPk)).toList();
    }


    @Override
    @Cacheable(value = "UserList", key = "'selectUserListByPkList-' + #p0.hashCode()")
    public List<User> selectUserListByPkList(List<Long> userPkList) {
        return userRepo.findAllByIdIn(userPkList);
    }


    @Override
    @Cacheable(value = "UserList", key = "'selectUserListByRole-' + #p0")
    public List<User> selectUserListByRole(Long rolePk) {
        List<Long> userPkList = roleService.selectUserPkListByRolePk(rolePk);
        return selectUserListByPkList(userPkList);
    }


    @Override
    @Cacheable(value = "UserList", key = "'selectUserListByRoleList-' + #p0.hashCode()")
    public List<User> selectUserListByRoleList(List<Role> roleList) {
        List<Long> rolePkList = roleList.stream().map(Role::getId).toList();
        List<Long> userPkList = roleService.selectUserPkListByRolePkList(rolePkList);
        return selectUserListByPkList(userPkList);
    }


    @Override
    public List<User> selectUserListByIdLike(String userId) {
        return selectUserList().stream().filter(User::isAvailable).filter(user -> user.getUserId().startsWith(userId)).toList();
    }


    @Override
    @Cacheable(value = "UserList", key = "'selectUserListBySuperAdminRole'")
    public List<User> selectUserListBySuperAdminRole() {
        return selectUserListByRole(RoleService.SUPER_ADMIN);
    }


    @Override
    @Cacheable(value = "UserProfileList", key = "'mapUserProfileListByUserPkList-' + #p0.hashCode()")
    public List<UserProfile> mapUserProfileListByUserPkList(List<Long> userPkList) {
        List<User> userList = selectUserListByPkList(userPkList);
        return mapUserProfileList(userList);
    }


    @Override
    @Cacheable(value = "UserProfileList", key = "'mapUserProfileList-' + #p0.hashCode()")
    public List<UserProfile> mapUserProfileList(List<User> userList) {
        Map<Long, Team> teamMap = teamService.selectTeamMap();
        List<Role> roleList = roleService.selectAll();
        List<UserRoleMap> userRoleMapList = roleService.selectUserRoleMap();
        return userList.stream()
                .map(user -> {
                    Long userPk = user.getId();
                    Long teamPk = user.getTeamPk();
                    Team team = teamMap.get(teamPk);
                    List<Long> rolePkList = userRoleMapList.stream()
                            .filter(userRoleMap -> userPk.equals(userRoleMap.getUserPk()))
                            .map(UserRoleMap::getRolePk).toList();
                    List<Role> userRoleList = roleList.stream().filter(role -> rolePkList.contains(role.getId())).toList();
                    return new UserProfile(user, team, userRoleList);
                }).toList();
    }


    @Override
    public Map<Long, User> selectUserMap() {
        List<User> userList = selectUserList();
        return userList.stream().collect(Collectors.toMap(User::getId, t -> t));
    }


    @Override
    public Map<Long, User> selectUserMapByPkList(List<Long> userPkList) {
        List<User> userList = selectUserListByPkList(userPkList);
        return userList.stream().collect(Collectors.toMap(User::getId, t -> t));
    }


    @Override
    public SearchCollectionResult<User> selectUserList(SearchUserRequest param) {
        Specification<User> specification = Specification.where(UserSpecification.search(param));
        Page<User> userPage = userRepo.findAll(specification, param.pageOf());
        List<User> userList = userPage.getContent();
        long total = userPage.getTotalElements();
        return SearchCollectionResult.<User>builder().collection(userList).foundRows(total).build();
    }


    @Override
    public SearchCollectionResult<UserProfile> selectUserProfileList(SearchUserRequest param) {
        Specification<User> specification = Specification.where(UserSpecification.search(param));
        Page<User> userPage = userRepo.findAll(specification, param.pageOf());
        List<User> userList = userPage.getContent();
        List<UserProfile> UserProfileList = mapUserProfileList(userList);
        long total = userPage.getTotalElements();
        return SearchCollectionResult.<UserProfile>builder().collection(UserProfileList).foundRows(total).build();
    }
}