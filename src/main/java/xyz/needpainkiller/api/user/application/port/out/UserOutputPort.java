package xyz.needpainkiller.api.user.application.port.out;

import xyz.needpainkiller.api.tenant.domain.error.TenantException;
import xyz.needpainkiller.api.tenant.domain.model.Tenant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 테넌트 관리 OutputPort
 *
 * @author needpainkiller6512
 * @version 1.0
 */
public interface UserOutputPort {

    /**
     * 테넌트 단일 검색
     * - UseYn 확인 안함
     *
     * @param tenantPk 테넌트 PK
     * @return Optional<Tenant> 테넌트
     */
    Optional<Tenant> selectTenant(Long tenantPk);

    /**
     * 테넌트 단일 검색
     * - UseYn 확인 함
     *
     * @param tenantPk 테넌트 PK
     * @return Optional<Tenant> 테넌트
     */
    Optional<Tenant> selectTenantAvailable(Long tenantPk);

    /**
     * 테넌트 목록 검색
     *
     * @return List<Tenant> 테넌트 목록
     */
    List<Tenant> selectTenantList();

    /**
     * 테넌트 PK 목록 검색
     *
     * @return List<Long> 테넌트 PK 목록
     */
    List<Long> selectTenantPkList();

    /**
     * 테넌트 맵 검색\
     * - key : 테넌트 PK
     * - value : 테넌트
     *
     * @return Map<Long, Tenant> 테넌트 맵
     */
    Map<Long, Tenant> selectTenantMap();


    /**
     * 테넌트 생성
     *
     * @param tenant 테넌트 생성 요청
     * @return Tenant 생성된 테넌트
     */
    Tenant create(Tenant tenant) throws TenantException;

    /**
     * 테넌트 수정
     *
     * @param tenant 테넌트 수정 요청
     * @return Tenant 수정된 테넌트
     */
    Tenant update(Tenant tenant) throws TenantException;

    /**
     * 테넌트 삭제
     *
     * @param tenant 테넌트 삭제 요청
     */
    Tenant delete(Tenant tenant) throws TenantException;

    /**
     * 테넌트 삭제
     *
     * @param tenantPk 테넌트 PK
     */
    Tenant delete(Long tenantPk) throws TenantException;
}
