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


    public void increaseLoginFailedCnt(Long userPk) {
        User user = userRepo.findUserById(userPk);
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
        user.setLoginFailedCnt(user.getLoginFailedCnt() + 1);
        userRepo.save(user);
    }


    public void updateLastLoginDate(Long userPk) {
        User user = userRepo.findUserById(userPk);
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
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

        userId = userId.trim();
        List<User> userList = userRepo.findUserByUserId(userId);
        boolean isUserExist = false;
        if (userList != null && !userList.isEmpty()) {
            isUserExist = userList.stream().filter(User::isAvailable).anyMatch(user -> user.getTenantPk().equals(tenantPk));
        }
        if (isUserExist) {
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

        User user = userRepo.findUserById(userPk);
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }

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
        User user = userRepo.findUserById(userPk);
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
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

        User user = userRepo.findUserById(userPk);
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
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
        User user = userRepo.findUserById(userPk);
        if (user == null) {
            throw new UserException(USER_NOT_EXIST);
        }
        user.setUserStatus(UserStatusType.OK);
        user.setUpdatedBy(SYSTEM_USER);
        user.setUpdatedDate(TimeHelper.now());
        userRepo.save(user);
    }
}