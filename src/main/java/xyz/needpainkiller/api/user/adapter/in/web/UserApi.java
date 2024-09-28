package xyz.needpainkiller.api.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.needpainkiller.api.user.adapter.in.web.data.UserRequests;

import java.io.IOException;
import java.util.Map;


@Tag(name = "9999001. 관리자 > 계정 관리", description = "USER")
@RequestMapping(value = "/api/v1/user", produces = {MediaType.APPLICATION_JSON_VALUE})
public interface UserApi {

    @Operation(description = "유저 리스트 조회")
    @PostMapping(value = "/list")
    ResponseEntity<Map<String, Object>> selectUserList(HttpServletRequest request, @RequestBody UserRequests.SearchUserRequest param);

    @Operation(description = "유저 리스트 파일 다운로드")
    @PostMapping(value = "/list/download")
    void downloadUserList(@RequestBody UserRequests.SearchUserRequest param, HttpServletRequest request, HttpServletResponse response) throws IOException;


    @Operation(description = "유저 조회")
    @GetMapping(value = "/{userPk}")
    ResponseEntity<Map<String, Object>> selectUser(@PathVariable("userPk") Long userPk, HttpServletRequest request);

    @Operation(description = "내 정보 조회")
    @GetMapping(value = "/me")
    ResponseEntity<Map<String, Object>> selectMe(HttpServletRequest request);

    @Operation(description = "유저 Id  중복확인")
    @GetMapping(value = "/exists/{userId}")
    ResponseEntity<Map<String, Object>> isUserIdExist(@NotBlank @PathVariable("userId") String userId, HttpServletRequest request);

    @PostMapping(value = "")
    @Operation(description = "유저등록 (관리자 급 권한을 가진 User 만 가능)")
    ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserRequests.UpsertUserRequest param, HttpServletRequest request);

    @PutMapping(value = "/{userPk}")
    @Operation(description = "유저 정보 변경 (관리자 급 권한을 가진 User 또는 본인 만 가능)")
    ResponseEntity<Map<String, Object>> updateUser(@NotBlank @PathVariable("userPk") Long userPk, @Valid @RequestBody UserRequests.UpsertUserRequest param, HttpServletRequest request);

    @DeleteMapping(value = "/{userPk}")
    @Operation(description = "유저 삭제 (관리자 급 권한을 가진 User 만 가능)")
    ResponseEntity<Map<String, Object>> deleteUser(@NotBlank @PathVariable("userPk") Long userPk, HttpServletRequest request);

/*    @PostMapping(value = "/{userPk}/validation")
    @Operation(description = "유저 이메일 인증 요청", hidden = true)
    ResponseEntity<Map<String, Object>> requestValidation(@NotBlank @PathVariable("userPk") Long userPk, HttpServletRequest request);

    @GetMapping(value = "/validation/{uuid}")
    @Operation(description = "유저 이메일 인증", hidden = true)
    ResponseEntity<Map<String, Object>> userValidation(@NotBlank @PathVariable("uuid") String uuid, HttpServletRequest request);


    @GetMapping(value = "/{userPk}/tempPassword")
    @Operation(description = "임시 패스워드 발급")
    ResponseEntity<Map<String, Object>> requestTempPasswordReset(@PathVariable("userPk") Long userPk, HttpServletRequest request);

    @GetMapping(value = "/{userId}/requestPasswordReset")
    @Operation(description = "패스워드 변경 요청", hidden = true)
    ResponseEntity<Map<String, Object>> requestPasswordReset(@PathVariable("userId") String userId, HttpServletRequest request);

    @PutMapping(value = "/updatePassword")
    @Operation(description = "패스워드 변경 처리", hidden = true)
    ResponseEntity<Map<String, Object>> updatePassword(AuthenticationRequests.ResetPasswordRequest param, HttpServletRequest request);*/
}