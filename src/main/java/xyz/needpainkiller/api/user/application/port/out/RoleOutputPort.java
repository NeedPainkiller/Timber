package xyz.needpainkiller.api.user.application.port.out;

import org.jetbrains.annotations.NotNull;
import xyz.needpainkiller.api.user.domain.error.RoleException;
import xyz.needpainkiller.api.user.domain.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * 권한 관리 OutputPort
 *
 * @author needpainkiller6512
 * @version 1.0
 */
public interface RoleOutputPort {

    /**
     * 권한 목록 검색
     *
     * @return List<Role> 권한 목록
     */
    List<Role> findAll();
    /**
     * 권한 목록 검색
     *
     * @param  idList 권한 Pk 리스트
     * @return List<Role> 권한 목록
     */
    List<Role> findAllByIdIn(List<Long> idList);
    /**
     * 권한 단일 검색
     * - UseYn 확인 안함
     *
     * @param rolePk 권한 PK
     * @return Optional<Role> 권한
     */
    Optional<Role> findRoleById(@NotNull Long rolePk);

    /**
     * 권한 단일 검색
     * - UseYn 확인 안함
     *
     * @param roleId 권한 아이디
     * @return Optional<Role> 권한
     */
    List<Role> findRoleByRoleId(@NotNull String roleId);

    /**
     * 권한 생성
     *
     * @param role 권한 생성 요청
     * @return Role 생성된 권한
     */
    Role create(Role role) throws RoleException;

    /**
     * 권한 수정
     *
     * @param role 권한 수정 요청
     * @return Role 수정된 권한
     */
    Role update(Role role) throws RoleException;

    /**
     * 권한 삭제
     *
     * @param role 권한 삭제 요청
     */
    Role delete(Role role) throws RoleException;

    /**
     * 권한 삭제
     *
     * @param rolePk 권한 PK
     */
    Role delete(Long rolePk) throws RoleException;
}
