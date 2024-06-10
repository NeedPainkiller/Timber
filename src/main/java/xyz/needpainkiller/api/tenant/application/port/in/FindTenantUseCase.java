package xyz.needpainkiller.api.tenant.application.port.in;

import reactor.core.publisher.Mono;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;

import java.util.List;
import java.util.Map;

/**
 * 테넌트 조회 UseCase
 *
 * @author needpainkiller6512
 * @version 1.0
 */
public interface FindTenantUseCase {

    /**
     * 테넌트 목록 조회
     *
     * @return List<Tenant> 테넌트 목록
     */
    List<Tenant> selectTenantList();

    /**
     * 공개 테넌트 목록 조회
     *
     * @return List<Tenant> 공개 테넌트 목록
     */
    List<Tenant> selectPublicTenantList();

    /**
     * 테넌트 Map 조회
     *
     * @return Map<Long, Tenant> 테넌트 Map
     */
    Map<Long, Tenant> selectTenantMap();

    /**
     * 테넌트 PK 목록 조회
     *
     * @return List<Long> 테넌트 PK 목록
     */
    List<Long> selectTenantPkList();

    /**
     * 테넌트 조회
     *
     * @param tenantPk 테넌트 PK
     * @return Tenant 테넌트
     */
    Tenant selectTenant(Long tenantPk);

    /**
     * 기본 테넌트 조회
     *
     * @return Tenant 기본 테넌트
     */
    Tenant selectDefatultTenant();

    /**
     * 기본 테넌트 조회 Mono
     *
     * @return Mono<Tenant> 기본 테넌트 Mono
     */
    Mono<Tenant> selectDefatultTenantMono();
}
