package xyz.needpainkiller.api.tenant.application.port.in;

import xyz.needpainkiller.api.user.domain.model.User;
import xyz.needpainkiller.api.tenant.adapter.in.web.data.CreateTenantRequest;
import xyz.needpainkiller.api.tenant.adapter.in.web.data.UpdateTenantRequest;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;

/**
 * 테넌트 관리 UseCase
 *
 * @author needpainkiller6512
 * @version 1.0
 */
public interface ManageTenantUseCase {

    /**
     * 테넌트 생성 UseCase
     *
     * @param param     생성할 테넌트 정보
     * @param requester 요청자
     * @return Tenant 생성된 테넌트
     */
    Tenant createTenant(CreateTenantRequest param, User requester);

    /**
     * 테넌트 수정 UseCase
     *
     * @param tenantPk  수정할 테넌트 PK
     * @param param     수정할 테넌트 정보
     * @param requester 요청자
     * @return Tenant 수정된 테넌트
     */
    Tenant updateTenant(Long tenantPk, UpdateTenantRequest param, User requester);

    /**
     * 테넌트 삭제 UseCase
     *
     * @param tenantPk  삭제할 테넌트 PK
     * @param requester 요청자
     */
    void deleteTenant(Long tenantPk, User requester);
}
