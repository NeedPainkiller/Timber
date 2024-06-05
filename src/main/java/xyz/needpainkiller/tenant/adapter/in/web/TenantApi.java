 package xyz.needpainkiller.tenant.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.needpainkiller.tenant.adapter.in.web.data.CreateTenantRequest;
import xyz.needpainkiller.tenant.adapter.in.web.data.UpdateTenantRequest;

import java.util.Map;

@Tag(name = "9999000. 관리자 > Tenant 관리", description = "TENANT")
@RequestMapping(value = "/api/v1/tenant", produces = {MediaType.APPLICATION_JSON_VALUE})
public interface TenantApi {

    @Operation(description = "공개 Tenant 리스트 조회")
    @GetMapping(value = "/public")
    ResponseEntity<Map<String, Object>> selectPublicTenantList(
            @RequestParam(name = "userId", defaultValue = "") String userId,
            HttpServletRequest request);

    @Operation(description = "전환 가능한 Tenant 리스트 조회")
    @GetMapping(value = "/switchable")
    ResponseEntity<Map<String, Object>> selectSwitchableTenantList(HttpServletRequest request);


    @Operation(description = "Tenant 리스트 조회")
    @GetMapping(value = "/list")
    ResponseEntity<Map<String, Object>> selectTenantList(HttpServletRequest request);

    @Operation(description = "Tenant 조회")
    @GetMapping(value = "/{tenantPk}")
    ResponseEntity<Map<String, Object>> selectTenant(
            @Parameter(name = "tenantPk", example = "10", required = true) @PathVariable("tenantPk") Long tenantPk,
            HttpServletRequest request);

    @Operation(description = "Tenant 등록")
    @PostMapping(value = "")
    ResponseEntity<Map<String, Object>> createTenant(
            @RequestBody @Valid CreateTenantRequest param, HttpServletRequest request);

    @Operation(description = "Tenant 수정")
    @PutMapping(value = "/{tenantPk}")
    ResponseEntity<Map<String, Object>> updateTenant(
            @Parameter(name = "tenantPk", example = "10", required = true) @PathVariable("tenantPk") Long tenantPk,
            @RequestBody @Valid UpdateTenantRequest param, HttpServletRequest request);

    @Operation(description = "Tenant 삭제")
    @DeleteMapping(value = "/{tenantPk}")
    ResponseEntity<Map<String, Object>> deleteTenant(
            @Parameter(name = "tenantPk", example = "10", required = true) @PathVariable("tenantPk") Long tenantPk, HttpServletRequest request);


    @Operation(description = "Tenant 전환")
    @PutMapping(value = "/{tenantPk}/switch")
    ResponseEntity<Map<String, Object>> switchTenant(
            @Parameter(name = "tenantPk", example = "10", required = true) @PathVariable("tenantPk") Long tenantPk, HttpServletRequest request, HttpServletResponse response);
}