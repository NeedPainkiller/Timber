package xyz.needpainkiller.api.user_hex.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.needpainkiller.api.user_hex.adapter.in.web.data.RoleRequests;

import java.util.Map;

@Tag(name = "9999002. 관리자 > 계정권한 관리", description = "ROLE")
@RequestMapping(value = "/api/v1/role", produces = {MediaType.APPLICATION_JSON_VALUE})
public interface RoleApi {

    @Operation(description = "모든 권한 정보 조회 (관리자 전용)", hidden = true)
    @GetMapping(value = "/all")
    @Deprecated
    ResponseEntity<Map<String, Object>> selectAllRoleList(HttpServletRequest request);

    @Operation(description = "권한 리스트 조회")
    @PostMapping(value = "/list")
    ResponseEntity<Map<String, Object>> selectRoleList(@Valid @RequestBody RoleRequests.SearchRoleRequest param, HttpServletRequest request);

    @Operation(description = "권한 리스트 다운로드")
    @PostMapping(value = "/list/download")
    void downloadRoleList(@Valid @RequestBody RoleRequests.SearchRoleRequest param, HttpServletRequest request, HttpServletResponse response);

    @Operation(description = "특정 권한정보 조회")
    @GetMapping(value = "/{rolePk}")
    ResponseEntity<Map<String, Object>> selectRole(@PathVariable("rolePk") Long rolePk, HttpServletRequest request);

    @Operation(description = "내 권한 조회")
    @GetMapping(value = "/me")
    ResponseEntity<Map<String, Object>> selectMyRoles(HttpServletRequest request);

    @PostMapping(value = "")
    @Operation(description = "권한등록 (관리자 급 권한을 가진 계정 만 가능)")
    ResponseEntity<Map<String, Object>> createRole(@Valid @RequestBody RoleRequests.UpsertRoleRequest param, HttpServletRequest request);

    @PutMapping(value = "/{rolePk}")
    @Operation(description = "권한 정보 변경 (관리자 급 권한을 가진 계정 만 가능)")
    ResponseEntity<Map<String, Object>> updateRole(@PathVariable("rolePk") Long rolePk, @Valid @RequestBody RoleRequests.UpsertRoleRequest param, HttpServletRequest request);

    @DeleteMapping(value = "/{rolePk}")
    @Operation(description = "권한 삭제 (관리자 급 권한을 가진 계정 만 가능)")
    ResponseEntity<Map<String, Object>> deleteRole(@PathVariable("rolePk") Long rolePk, HttpServletRequest request);

    @GetMapping(value = "/api")
    @Operation(description = "전체 API 정보 조회")
    ResponseEntity<Map<String, Object>> selectAllApiList(HttpServletRequest request);

}