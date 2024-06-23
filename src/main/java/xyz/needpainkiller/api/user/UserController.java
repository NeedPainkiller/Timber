package xyz.needpainkiller.api.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import xyz.needpainkiller.api.authentication.AuthenticationService;
import xyz.needpainkiller.api.user.dto.UserCsv;
import xyz.needpainkiller.api.user.dto.UserProfile;
import xyz.needpainkiller.api.user.dto.UserRequests;
import xyz.needpainkiller.api.user_hex.domain.error.RoleException;
import xyz.needpainkiller.api.user_hex.domain.error.UserException;
import xyz.needpainkiller.api.user_hex.domain.model.Role;
import xyz.needpainkiller.api.user_hex.domain.model.User;
import xyz.needpainkiller.common.controller.CommonController;
import xyz.needpainkiller.common.dto.SearchCollectionResult;
import xyz.needpainkiller.helper.ValidationHelper;
import xyz.needpainkiller.lib.sheet.SpreadSheetService;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;
import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.ROLE_NOT_EXIST;
import static xyz.needpainkiller.lib.exceptions.CommonErrorCode.USER_DELETE_SELF;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController extends CommonController implements UserApi {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private SpreadSheetService sheetService;

    @Override
    public ResponseEntity<Map<String, Object>> selectUserList(HttpServletRequest request, UserRequests.SearchUserRequest param) throws UserException {
        Map<String, Object> model = new HashMap<>();
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        param.setTenantPk(tenantPk);
        SearchCollectionResult<UserProfile> result = userService.selectUserProfileList(param);
        model.put(KEY_LIST, result.getCollection());
        model.put(KEY_TOTAL, result.getFoundRows());
        return ok(model);
    }

    @Override
    public void downloadUserList(UserRequests.SearchUserRequest param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        param.setTenantPk(tenantPk);
        param.setIsPagination(false);
        SearchCollectionResult<UserProfile> result = userService.selectUserProfileList(param);
        Collection<UserProfile> userProfiles = result.getCollection();
        List<UserCsv> userCsvList = userProfiles.stream().map(UserCsv::new).toList();
        sheetService.downloadExcel(UserCsv.class, userCsvList, response);
    }


    @Override
    public ResponseEntity<Map<String, Object>> selectUser(Long userPk, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        UserProfile userProfile = userService.selectUserProfile(userPk);
        model.put(KEY_USER, userProfile);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> selectMe(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        User user = authenticationService.getUserByToken(request);
        UserProfile userProfile = userService.selectUserProfile(user);
        model.put(KEY_USER, userProfile);
        return ok(model);
    }


    @Override
    public ResponseEntity<Map<String, Object>> isUserIdExist(String userId, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        Long tenant = authenticationService.getTenantPkByToken(request);
        ValidationHelper.checkAnyRequiredEmpty(userId);
        model.put(KEY_EXIST, userService.isUserIdExist(tenant, userId));
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> createUser(UserRequests.UpsertUserRequest param, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();

        List<Role> requestRoleList = roleService.selectRolesByPkList(param.getRoles());
        if (requestRoleList.isEmpty()) {
            throw new RoleException(ROLE_NOT_EXIST);
        }

        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        param.setTenantPk(tenantPk);
        List<Role> authority = authenticationService.getRoleListByToken(request);
        roleService.checkRequestRoleAuthority(requestRoleList, authority);

        User savedUser = userService.createUser(param, requestRoleList, requester);
        UserProfile userProfile = userService.selectUserProfile(savedUser);
        model.put(KEY_USER, userProfile);
        model.put(KEY_ROLE_LIST, userProfile.getRoleList());
        return status(HttpStatus.CREATED).body(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateUser(Long userPk, UserRequests.UpsertUserRequest param, HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        List<Role> requestRoleList = roleService.selectRolesByPkList(param.getRoles());
        if (requestRoleList.isEmpty()) {
            throw new RoleException(ROLE_NOT_EXIST);
        }
        User requester = authenticationService.getUserByToken(request);
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        param.setTenantPk(tenantPk);
        List<Role> authority = authenticationService.getRoleListByToken(request);
        roleService.checkRequestRoleAuthority(requestRoleList, authority);

        User savedUser = userService.updateUser(userPk, param, requestRoleList, requester);
        UserProfile userDetail = userService.selectUserProfile(savedUser);
        model.put(KEY_USER, userDetail);
        model.put(KEY_ROLE_LIST, userDetail.getRoleList());
        return status(HttpStatus.CREATED).body(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteUser(Long userPk, HttpServletRequest request) throws UserException {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        if (userPk.equals(requester.getId())) {
            throw new UserException(USER_DELETE_SELF);
        }
        Long tenantPk = authenticationService.getTenantPkByToken(request);
        userService.deleteUser(tenantPk, userPk, requester);
        return status(HttpStatus.NO_CONTENT).body(model);
    }

/*@Override
    public ResponseEntity<Map<String, Object>> requestValidation(Long userPk, HttpServletRequest request) throws UserException {
        User user = userService.selectUser(userPk);
        UserStatusType userStatusType = user.getStatus();
        if (UserStatusType.OK.equals(userStatusType)) {
            throw new UserException(VERIFICATION_ALREADY_DONE);
        }
        if (UserStatusType.NOT_VERIFIED.equals(userStatusType)) {
            UserVerificationCode userVerificationCode = userVerificationService.createVerificationCode(user);
            userVerificationService.sendVerificationMail(user.getUserEmail(), userVerificationCode.getVerificationUuid());
        }

        return ok().build();
    }

    @Override
    public ResponseEntity<Map<String, Object>> userValidation(String uuid, HttpServletRequest request) throws UserException {
        Map<String, Object> model = new HashMap<>();
        if (Strings.isBlank(uuid)) {
            throw new UserException(VERIFICATION_NOT_MATCH);
        }
        UserVerificationCode userVerificationCode = userVerificationService.verifyCode(uuid);
        int userPk = userVerificationCode.getUserPk();
        model.put(KEY_USER, userService.enableUser(userPk));
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> requestTempPasswordReset(Long userPk, HttpServletRequest request) throws UserException {
        Map<String, Object> model = new HashMap<>();
        User requester = authenticationService.getUserByToken(request);
        String userPwd = userService.updateTempPassword(userPk, requester);
        model.put("password", userPwd);
        return ok(model);
    }

    @Override
    public ResponseEntity<Map<String, Object>> requestPasswordReset(String userId, HttpServletRequest request) throws UserException {
        User user;
        if (Strings.isBlank(userId)) {
            user = authenticationService.getUserByToken(request);
        } else {
            List<Role> role = authenticationService.getRoleByToken(request);
            if (!(roleService.hasAdminRole(role))) {//관리자 권한 아님
                throw new UserException(USER_FORBIDDEN);
            }
            user = userService.selectUser(userId);
        }
        UserVerificationCode userVerificationCode = userVerificationService.createVerificationCode(user);
        userVerificationService.sendVerificationMail(user.getUserEmail(), userVerificationCode.getVerificationUuid());
        return ok().build();
    }

    @Override
    public ResponseEntity<Map<String, Object>> updatePassword(AuthenticationRequests.ResetPasswordRequest param, HttpServletRequest request) {
        String uuid = param.getUuid();
        String password = param.getPassword();
        ValidationHelper.checkPassword(password);
        UserVerificationCode userVerificationCode = userVerificationService.verifyCode(uuid);
//        userService.updatePassword(userVerificationCode.getUserPk(), password);
        return ok().build();
    }*/

}
