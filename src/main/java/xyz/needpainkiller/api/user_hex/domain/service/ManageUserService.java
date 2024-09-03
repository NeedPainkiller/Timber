package xyz.needpainkiller.api.user_hex.domain.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.needpainkiller.api.team.TeamService;
import xyz.needpainkiller.api.team.error.TeamException;
import xyz.needpainkiller.api.team.model.Team;
import xyz.needpainkiller.api.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;
import xyz.needpainkiller.api.user.RoleService;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.UserRequests.SearchUserRequest;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.UserRequests.UpsertUserRequest;
import xyz.needpainkiller.api.user_hex.adapter.out.persistence.repository.UserRepo;
import xyz.needpainkiller.api.user_hex.adapter.out.persistence.repository.UserSpecification;
import xyz.needpainkiller.api.user_hex.adapter.out.web.data.UserProfile;
import xyz.needpainkiller.api.user_hex.domain.error.UserException;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.api.user_hex.domain.model.UserRoleMap;
import xyz.needpainkiller.api.user_hex.domain.model.UserStatusType;
import xyz.needpainkiller.common.dto.SearchCollectionResult;
import xyz.needpainkiller.helper.TimeHelper;
import xyz.needpainkiller.helper.ValidationHelper;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static xyz.needpainkiller.api.tenant.domain.error.TenantErrorCode.TENANT_CONFLICT;
import static xyz.needpainkiller.api.user_hex.domain.error.UserErrorCode.USER_ALREADY_EXIST;
import static xyz.needpainkiller.api.user_hex.domain.error.UserErrorCode.USER_NOT_EXIST;

@Slf4j
@Service
public class ManageUserService {

    private Long SYSTEM_USER = 1L;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleService roleService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostConstruct
    public void init() {
        log.info("CommonUserService");
        log.info("SYSTEM_USER: {}", SYSTEM_USER);
    }


    @Cacheable(value = "User", key = "'selectSystemUser'")
    public User selectSystemUser() {
        return selectUser(SYSTEM_USER);
    }


    public Long selectSystemUserPk() {
        return SYSTEM_USER;
    }


    public User selectUser(Long userPk) throws UserException {
        User user = userRepo.findUserById(userPk);
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
        return user;
    }


    public List<User> selectUserByUserId(String userId) throws UserException {
        userId = userId.trim();
        List<User> userList = userRepo.findUserByUserId(userId);
        if (userList == null || userList.isEmpty()) {
            throw new UserException(USER_NOT_EXIST);
        }
        return userList;
    }


    public User selectUserByUserId(Tenant tenant, String userId) throws UserException {
        Long tenantPk = tenant.getId();
        return selectUserByUserId(tenantPk, userId);
    }


    public User selectUserByUserId(Long tenantPk, String userId) throws UserException {
        userId = userId.trim();
        List<User> userList = userRepo.findUserByUserId(userId);
        if (userList == null || userList.isEmpty()) {
            throw new UserException(USER_NOT_EXIST);
        }
        return userList.stream().filter(User::isAvailable).filter(user -> user.filterByTenant(tenantPk)).findAny()
                .orElseThrow(() -> new UserException(USER_NOT_EXIST));
    }


    public boolean isUserIdExist(Long tenantPk, String userId) {
        userId = userId.trim();
        List<User> userList = userRepo.findUserByUserId(userId);
        if (userList == null || userList.isEmpty()) {
            return false;
        }
        return userList.stream().filter(User::isAvailable).anyMatch(user -> user.getTenantPk().equals(tenantPk));
    }


    @Cacheable(value = "UserProfile", key = "'selectUserProfile-' + #p0", unless = "#result == null")
    public UserProfile selectUserProfile(Long userPk) throws UserException {
        User user = userRepo.findUserById(userPk);
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
        return selectUserProfile(user);
    }


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


    @Cacheable(value = "UserList", key = "'selectUserList'")
    public List<User> selectUserList() {
        return userRepo.findAll();
    }


    @Cacheable(value = "UserList", key = "'selectUserList-' + #p0")
    public List<User> selectUserList(Long tenantPk) {
        return userRepo.findAll().stream().filter(user -> user.filterByTenant(tenantPk)).toList();
    }


    @Cacheable(value = "UserList", key = "'selectUserListByPkList-' + #p0.hashCode()")
    public List<User> selectUserListByPkList(List<Long> userPkList) {
        return userRepo.findAllByIdIn(userPkList);
    }


    @Cacheable(value = "UserList", key = "'selectUserListByRole-' + #p0")
    public List<User> selectUserListByRole(Long rolePk) {
        List<Long> userPkList = roleService.selectUserPkListByRolePk(rolePk);
        return selectUserListByPkList(userPkList);
    }


    @Cacheable(value = "UserList", key = "'selectUserListByRoleList-' + #p0.hashCode()")
    public List<User> selectUserListByRoleList(List<Role> roleList) {
        List<Long> rolePkList = roleList.stream().map(Role::getId).toList();
        List<Long> userPkList = roleService.selectUserPkListByRolePkList(rolePkList);
        return selectUserListByPkList(userPkList);
    }


    public List<User> selectUserListByIdLike(String userId) {
        return selectUserList().stream().filter(User::isAvailable).filter(user -> user.getUserId().startsWith(userId)).toList();
    }


    @Cacheable(value = "UserList", key = "'selectUserListBySuperAdminRole'")
    public List<User> selectUserListBySuperAdminRole() {
        return selectUserListByRole(RoleService.SUPER_ADMIN);
    }


    @Cacheable(value = "UserProfileList", key = "'mapUserProfileListByUserPkList-' + #p0.hashCode()")
    public List<UserProfile> mapUserProfileListByUserPkList(List<Long> userPkList) {
        List<User> userList = selectUserListByPkList(userPkList);
        return mapUserProfileList(userList);
    }


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


    public Map<Long, User> selectUserMap() {
        List<User> userList = selectUserList();
        return userList.stream().collect(Collectors.toMap(User::getId, t -> t));
    }


    public Map<Long, User> selectUserMapByPkList(List<Long> userPkList) {
        List<User> userList = selectUserListByPkList(userPkList);
        return userList.stream().collect(Collectors.toMap(User::getId, t -> t));
    }


    public SearchCollectionResult<User> selectUserList(SearchUserRequest param) {
        Specification<User> specification = Specification.where(UserSpecification.search(param));
        Page<User> userPage = userRepo.findAll(specification, param.pageOf());
        List<User> userList = userPage.getContent();
        long total = userPage.getTotalElements();
        return SearchCollectionResult.<User>builder().collection(userList).foundRows(total).build();
    }


    public SearchCollectionResult<UserProfile> selectUserProfileList(SearchUserRequest param) {
        Specification<User> specification = Specification.where(UserSpecification.search(param));
        Page<User> userPage = userRepo.findAll(specification, param.pageOf());
        List<User> userList = userPage.getContent();
        List<UserProfile> UserProfileList = mapUserProfileList(userList);
        long total = userPage.getTotalElements();
        return SearchCollectionResult.<UserProfile>builder().collection(UserProfileList).foundRows(total).build();
    }


    public void increaseLoginFailedCnt(Long userPk) {
        User user = selectUser(userPk);
        user.setLoginFailedCnt(user.getLoginFailedCnt() + 1);
        userRepo.save(user);
    }


    public void updateLastLoginDate(Long userPk) {
        User user = selectUser(userPk);
        user.setLoginFailedCnt(0);
        user.setLastLoginDate(TimeHelper.now());
        userRepo.save(user);
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "User", allEntries = true),
            @CacheEvict(value = "UserList", allEntries = true),
            @CacheEvict(value = "UserRole", allEntries = true)
    })
    public User createUser(UpsertUserRequest param, List<Role> roleList, User requester) throws UserException {
        Long requesterPk = requester.getId();
        Long tenantPk = param.getTenantPk();

        String userId = param.getUserId();
        String userEmail = param.getUserEmail();
        String userNm = param.getUserName();
        String userPwd = param.getUserPwd();

        Long teamPk = param.getTeamPk();
        teamService.selectTeam(teamPk);

        ValidationHelper.checkUserData(userId, userNm, userPwd);
        ValidationHelper.checkAnyRequiredEmpty(userEmail, userNm, userId);

        if (isUserIdExist(tenantPk, userId)) {
            throw new UserException(USER_ALREADY_EXIST);
        }

        if (roleList.stream().map(Role::getTenantPk).anyMatch(roleTenantPk -> !Objects.equals(roleTenantPk, tenantPk))) {
            throw new TenantException(TENANT_CONFLICT);
        }

        User user = new User();
        user.setTenantPk(tenantPk);
        user.setUseYn(true);
        user.setUserId(userId);
        user.setUserEmail(userEmail);
        user.setUserName(userNm);
        user.setUserPwd(bCryptPasswordEncoder.encode(userPwd));
        user.setTeamPk(teamPk);
        user.setUserStatus(param.getUserStatusType());
        user.setCreatedBy(requesterPk);
        user.setCreatedDate(TimeHelper.now());
        user.setUpdatedBy(requesterPk);
        user.setUpdatedDate(TimeHelper.now());
        user.setLoginFailedCnt(0);
        Map<String, Serializable> data = param.getData();
        user.setData(Objects.requireNonNullElseGet(data, HashMap::new));

        user = userRepo.save(user);
        roleService.upsertUserRole(user.getId(), roleList);
        return user;
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "User", allEntries = true),
            @CacheEvict(value = "UserList", allEntries = true),
            @CacheEvict(value = "UserRole", allEntries = true)
    })
    public User updateUser(Long userPk, UpsertUserRequest param, List<Role> roleList, User requester) {
        String userId = param.getUserId();
        String userEmail = param.getUserEmail();
        String userName = param.getUserName();
        String userPwd = param.getUserPwd();

        Long requesterPk = requester.getId();
        Long tenantPk = param.getTenantPk();

        Long teamPk = param.getTeamPk();
        teamService.selectTeam(teamPk);

        ValidationHelper.checkUserData(userId, userName);

        if (roleList.stream().map(Role::getTenantPk).anyMatch(roleTenantPk -> !Objects.equals(roleTenantPk, tenantPk))) {
            throw new TenantException(TENANT_CONFLICT);
        }

        User user = selectUser(userPk);

        if (!user.getTenantPk().equals(tenantPk)) {
            throw new TenantException(TENANT_CONFLICT);
        }

        user.setTenantPk(tenantPk);
        user.setUseYn(true);
        user.setUserId(userId);
        user.setUserEmail(userEmail);
        user.setUserName(userName);
        user.setTeamPk(teamPk);
        user.setUserStatus(param.getUserStatusType());
        user.setUpdatedBy(requesterPk);
        user.setUpdatedDate(TimeHelper.now());
        user.setLoginFailedCnt(0);
        user.setData(param.getData());
        boolean updatePassword = !Strings.isBlank(userPwd);
        if (updatePassword) {
            updatePassword(userPk, requesterPk, userPwd);
        }
        user = userRepo.save(user);
        roleService.upsertUserRole(userPk, roleList);
        return user;
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "User", allEntries = true),
            @CacheEvict(value = "UserList", allEntries = true),
            @CacheEvict(value = "UserRole", allEntries = true)
    })
    public void updatePassword(Long userPk, Long requesterPk, String userPwd) {
        ValidationHelper.checkPassword(userPwd);
        User user = selectUser(userPk);
        user.setUserPwd(bCryptPasswordEncoder.encode(userPwd));
        user.setUpdatedBy(requesterPk);
        user.setUpdatedDate(TimeHelper.now());
        userRepo.save(user);
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "User", allEntries = true),
            @CacheEvict(value = "UserList", allEntries = true),
            @CacheEvict(value = "UserRole", allEntries = true)
    })
    public void deleteUser(Long tenantPk, Long userPk, User requester) {
        Long requesterPk = requester.getId();

        User user = selectUser(userPk);
        if (!user.getTenantPk().equals(tenantPk)) {
            throw new TenantException(TENANT_CONFLICT);
        }

        user.setUseYn(false);
        user.setUserId(user.getUserId() + "-" + UUID.randomUUID());
        user.setUserStatus(UserStatusType.NOT_USED);
        user.setUpdatedBy(requesterPk);
        user.setUpdatedDate(TimeHelper.now());
        userRepo.save(user);
        roleService.deleteUserRole(userPk);
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "User", allEntries = true),
            @CacheEvict(value = "UserList", allEntries = true),
            @CacheEvict(value = "UserRole", allEntries = true)
    })
    public void enableUser(Long userPk) {
        User user = selectUser(userPk);
        user.setUserStatus(UserStatusType.OK);
        user.setUpdatedBy(SYSTEM_USER);
        user.setUpdatedDate(TimeHelper.now());
        userRepo.save(user);
    }
}