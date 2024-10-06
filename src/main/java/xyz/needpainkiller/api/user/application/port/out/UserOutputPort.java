package xyz.needpainkiller.api.user.application.port.out;

import org.jetbrains.annotations.NotNull;
import xyz.needpainkiller.api.user.domain.error.UserException;
import xyz.needpainkiller.api.user.domain.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 유저 관리 OutputPort
 *
 * @author needpainkiller6512
 * @version 1.0
 */
public interface UserOutputPort {

    /**
     * 유저 목록 검색
     *
     * @return List<User> 유저 목록
     */
    List<User> findAll();
    /**
     * 유저 목록 검색
     *
     * @param  idList 유저 Pk 리스트
     * @return List<User> 유저 목록
     */
    List<User> findAllByIdIn(List<Long> idList);
    /**
     * 유저 단일 검색
     * - UseYn 확인 안함
     *
     * @param userPk 유저 PK
     * @return Optional<User> 유저
     */
    Optional<User> findUserById(@NotNull Long userPk);

    /**
     * 유저 단일 검색
     * - UseYn 확인 안함
     *
     * @param userId 유저 아이디
     * @return Optional<User> 유저
     */
    List<User> findUserByUserId(@NotNull String userId);

    /**
     * 유저 생성
     *
     * @param user 유저 생성 요청
     * @return User 생성된 유저
     */
    User create(User user) throws UserException;

    /**
     * 유저 수정
     *
     * @param user 유저 수정 요청
     * @return User 수정된 유저
     */
    User update(User user) throws UserException;

    /**
     * 유저 삭제
     *
     * @param user 유저 삭제 요청
     */
    User delete(User user) throws UserException;

    /**
     * 유저 삭제
     *
     * @param userPk 유저 PK
     */
    User delete(Long userPk) throws UserException;
}
